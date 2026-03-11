package com.alilopez.kt_demohilt.features.workoutplans.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetLocalExercisesUseCase
import com.alilopez.kt_demohilt.features.workoutplans.domain.usecases.AddExerciseToPlanUseCase
import com.alilopez.kt_demohilt.features.workoutplans.domain.usecases.GetPlanExercisesUseCase
import com.alilopez.kt_demohilt.features.workoutplans.domain.usecases.RemoveExerciseFromPlanUseCase
import com.alilopez.kt_demohilt.features.workoutplans.presentation.screens.WorkoutDetailUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
    private val getPlanExercisesUseCase: GetPlanExercisesUseCase,
    private val getLocalExercisesUseCase: GetLocalExercisesUseCase,
    private val addExerciseToPlanUseCase: AddExerciseToPlanUseCase,
    private val removeExerciseFromPlanUseCase: RemoveExerciseFromPlanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutDetailUIState())
    val uiState: StateFlow<WorkoutDetailUIState> = _uiState.asStateFlow()

    fun loadPlanExercises(planId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getPlanExercisesUseCase(planId).fold(
                onSuccess = { exercises ->
                    _uiState.update { it.copy(isLoading = false, exercises = exercises) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun loadAvailableExercises() {
        viewModelScope.launch {
            getLocalExercisesUseCase().fold(
                onSuccess = { allLocals: List<Exercise> ->
                    val currentIds = _uiState.value.exercises.map { it.exerciseId }
                    val available = allLocals.filter { it.exerciseId !in currentIds }
                    _uiState.update { it.copy(availableLocalExercises = available, isAddingExercise = true) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(errorMessage = error.message) }
                }
            )
        }
    }

    fun addExerciseToPlan(planId: Int, exerciseId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val idInt = exerciseId.toIntOrNull() ?: 0
            addExerciseToPlanUseCase(planId, idInt).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, exerciseAdded = true, isAddingExercise = false) }
                    loadPlanExercises(planId)
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun removeExerciseFromPlan(planId: Int, exerciseId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val idInt = exerciseId.toIntOrNull() ?: 0
            removeExerciseFromPlanUseCase(planId, idInt).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, exerciseRemoved = true) }
                    loadPlanExercises(planId)
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun closeAddExercise() {
        _uiState.update { it.copy(isAddingExercise = false) }
    }

    fun resetStatus() {
        _uiState.update { it.copy(exerciseAdded = false, exerciseRemoved = false) }
    }
}
