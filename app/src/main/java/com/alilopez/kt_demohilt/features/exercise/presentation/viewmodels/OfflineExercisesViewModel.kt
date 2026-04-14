package com.alilopez.kt_demohilt.features.exercise.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetOfflineExercisesUseCase
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.RemoveOfflineExerciseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OfflineExercisesUIState(
    val isLoading: Boolean = true,
    val exercises: List<Exercise> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class OfflineExercisesViewModel @Inject constructor(
    private val getOfflineExercisesUseCase: GetOfflineExercisesUseCase,
    private val removeOfflineExerciseUseCase: RemoveOfflineExerciseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OfflineExercisesUIState())
    val uiState: StateFlow<OfflineExercisesUIState> = _uiState.asStateFlow()

    init {
        loadOfflineExercises()
    }

    private fun loadOfflineExercises() {
        viewModelScope.launch {
            getOfflineExercisesUseCase()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar ejercicios offline"
                    )
                }
                .collect { exercises ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        exercises = exercises,
                        error = null
                    )
                }
        }
    }

    fun onRemoveExercise(exerciseId: Int) {
        viewModelScope.launch {
            try {
                removeOfflineExerciseUseCase(exerciseId)
                // No need to manually update state, as the Flow from Room will emit a new list
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error al eliminar ejercicio"
                )
            }
        }
    }
}
