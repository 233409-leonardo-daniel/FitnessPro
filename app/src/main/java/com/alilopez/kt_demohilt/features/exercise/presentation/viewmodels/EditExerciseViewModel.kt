package com.alilopez.kt_demohilt.features.exercise.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetExerciseByIdUseCase
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.UpdateLocalExerciseUseCase
import com.alilopez.kt_demohilt.features.exercise.presentation.screens.EditExerciseUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditExerciseViewModel @Inject constructor(
    private val getExerciseByIdUseCase: GetExerciseByIdUseCase,
    private val updateLocalExerciseUseCase: UpdateLocalExerciseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditExerciseUiState())
    val uiState: StateFlow<EditExerciseUiState> = _uiState.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _instructions = MutableStateFlow("")
    val instructions: StateFlow<String> = _instructions.asStateFlow()

    private val _selectedExerciseType = MutableStateFlow<String?>(null)
    val selectedExerciseType: StateFlow<String?> = _selectedExerciseType.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow("")
    val selectedDifficulty: StateFlow<String> = _selectedDifficulty.asStateFlow()

    private val _selectedDays = MutableStateFlow<List<String>>(emptyList())
    val selectedDays: StateFlow<List<String>> = _selectedDays.asStateFlow()

    fun loadExercise(exerciseId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = getExerciseByIdUseCase(exerciseId)
            result.fold(
                onSuccess = { exercise ->
                    _name.value = exercise.name
                    _description.value = exercise.description.orEmpty()
                    _instructions.value = exercise.instructions.joinToString("\n")
                    _selectedExerciseType.value = exercise.exerciseType
                    _selectedDifficulty.value = exercise.difficulty ?: "Facil"
                    _selectedDays.value = exercise.scheduledDays

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            exercise = exercise
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al cargar el ejercicio"
                        )
                    }
                }
            )
        }
    }

    fun onNameChange(value: String) {
        _name.value = value
    }

    fun onDescriptionChange(value: String) {
        _description.value = value
    }

    fun onInstructionsChange(value: String) {
        _instructions.value = value
    }

    fun onExerciseTypeChange(type: String?) {
        _selectedExerciseType.value = type
    }

    fun onDifficultyChange(difficulty: String) {
        _selectedDifficulty.value = difficulty
    }

    fun onDayToggle(day: String) {
        _selectedDays.value = if (_selectedDays.value.contains(day)) {
            _selectedDays.value - day
        } else {
            _selectedDays.value + day
        }
    }

    fun updateExercise(exerciseId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val currentExercise = _uiState.value.exercise
            if (currentExercise == null) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "No se pudo obtener el ejercicio a editar")
                }
                return@launch
            }

            try {
                updateLocalExerciseUseCase(
                    exerciseId = exerciseId,
                    name = _name.value,
                    description = _description.value,
                    scheduledDays = _selectedDays.value,
                    bodyparts = currentExercise.bodyparts,
                    equipment = currentExercise.equipments,
                    targetMuscles = currentExercise.targetMuscles,
                    secondaryMuscles = currentExercise.secondaryMuscles,
                    exerciseType = _selectedExerciseType.value,
                    instructions = _instructions.value.ifBlank { null },
                    difficulty = _selectedDifficulty.value.ifBlank { "Facil" },
                    imageUrl = currentExercise.gifUrl.ifBlank { null }
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        exerciseUpdated = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al actualizar el ejercicio"
                    )
                }
            }
        }
    }

    fun resetExerciseUpdated() {
        _uiState.update { it.copy(exerciseUpdated = false) }
    }
}

