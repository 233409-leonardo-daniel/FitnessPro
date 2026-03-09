package com.alilopez.kt_demohilt.features.exercise.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetExercisesUseCase
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetExercisesByBodyPartUseCase
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetLocalExercisesUseCase
import com.alilopez.kt_demohilt.features.exercise.presentation.screens.ExercisesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val getExercisesUseCase: GetExercisesUseCase,
    private val getExercisesByBodyPartUseCase: GetExercisesByBodyPartUseCase,
    private val getLocalExercisesUseCase: GetLocalExercisesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExercisesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadExercises()
        loadLocalExercises()
    }

    fun loadExercises() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = getExercisesUseCase()
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

    fun loadLocalExercises() {
        viewModelScope.launch {
            val result = getLocalExercisesUseCase()
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { list ->
                        currentState.copy(localExercises = list)
                    },
                    onFailure = { _ ->
                        currentState.copy(localExercises = emptyList())
                    }
                )
            }
        }
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

    fun toggleFilter() {
        _uiState.update { it.copy(isFilterExpanded = !it.isFilterExpanded) }
    }

    fun onBodyPartChecked(bodyPart: String, isChecked: Boolean) {
        _uiState.update {
            it.copy(
                selectedBodyPart = if (isChecked) bodyPart else null
            )
        }
    }

    fun applyFilters() {
        val selectedBodyPart = _uiState.value.selectedBodyPart
        _uiState.update { it.copy(isFilterExpanded = false) }

        if (selectedBodyPart != null) {
            loadExercisesByBodyPart(selectedBodyPart)
        } else {
            loadExercises()
        }
    }

    fun clearFilters() {
        _uiState.update { it.copy(selectedBodyPart = null) }
        loadExercises()
    }
}
