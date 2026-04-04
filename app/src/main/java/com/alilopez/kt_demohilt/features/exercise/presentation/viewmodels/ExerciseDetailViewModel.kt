package com.alilopez.kt_demohilt.features.exercise.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetExerciseByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseDetailUiState(
    val isLoading: Boolean = false,
    val exercise: Exercise? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    private val getExerciseByIdUseCase: GetExerciseByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseDetailUiState())
    val uiState: StateFlow<ExerciseDetailUiState> = _uiState.asStateFlow()

    fun loadExercise(exerciseId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getExerciseByIdUseCase(exerciseId).fold(
                onSuccess = { exercise ->
                    _uiState.update { it.copy(isLoading = false, exercise = exercise) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error desconocido") }
                }
            )
        }
    }
}
