package com.alilopez.kt_demohilt.features.exercise.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.domain.entities.ExerciseFilter
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetExercisesByBodyPartUseCase
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetExercisesUseCase
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetLocalExercisesByFilterUseCase
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetLocalExercisesUseCase
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.SyncExercisesByFilterUseCase
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.SyncExercisesUseCase
import com.alilopez.kt_demohilt.features.exercise.presentation.screens.ExercisesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val getExercisesUseCase: GetExercisesUseCase,
    private val getExercisesByBodyPartUseCase: GetExercisesByBodyPartUseCase,
    private val getLocalExercisesUseCase: GetLocalExercisesUseCase,
    private val syncExercisesUseCase: SyncExercisesUseCase,
    private val syncExercisesByFilterUseCase: SyncExercisesByFilterUseCase,
    private val getLocalExercisesByFilterUseCase: GetLocalExercisesByFilterUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExercisesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadExercises()
        loadLocalExercises()
        syncExercises() // <-- Agregamos esto para que sincronice al iniciar
    }

    fun loadExercises() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = getExercisesUseCase()
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { list -> currentState.copy(isLoading = false, exercises = list) },
                    onFailure = { error -> currentState.copy(isLoading = false, error = error.message) }
                )
            }
        }
    }

    fun loadLocalExercises() {
        getLocalExercisesUseCase.invoke().onEach { exercises ->
            _uiState.update { it.copy(localExercises = exercises, isLoading = false) }
        }.launchIn(viewModelScope)
    }

    private fun loadExercisesByBodyPart(bodyPart: String) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = getExercisesByBodyPartUseCase(bodyPart)
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { list ->
                        currentState.copy(isLoading = false, exercises = list)
                    },
                    onFailure = { error ->
                        currentState.copy(isLoading = false, error = error.message)
                    }
                )
            }
        }
    }

    fun syncExercises() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            syncExercisesUseCase()
            _uiState.update { it.copy(isSyncing = false) }
        }
    }

    fun toggleFilter() {
        _uiState.update { it.copy(isFilterExpanded = !it.isFilterExpanded) }
    }

    fun onBodyPartChecked(bodyPart: String, isChecked: Boolean) {
        _uiState.update {
            it.copy(selectedBodyPart = if (isChecked) bodyPart else null)
        }
    }

    fun applyFilters() {
        val filter = ExerciseFilter(
            bodyPart = _uiState.value.selectedBodyPart
            // difficulty = _uiState.value.selectedDifficulty,
            // exerciseType = _uiState.value.selectedExerciseType
        )
        _uiState.update { it.copy(isFilterExpanded = false) }

        if (!filter.isEmpty) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, exercises = emptyList(), isFiltered = true) }
                syncExercisesByFilterUseCase(filter)
                getLocalExercisesByFilterUseCase(filter)
                    .onEach { exercises: List<Exercise> ->
                        _uiState.update { state -> state.copy(isLoading = false, exercises = exercises) }
                    }
                    .launchIn(viewModelScope)
            }
        } else {
            loadExercises()
        }
    }

    fun clearFilters() {
        _uiState.update { it.copy(selectedBodyPart = null, isFiltered = false) }
        loadExercises()
    }
}
