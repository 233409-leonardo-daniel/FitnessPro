package com.alilopez.kt_demohilt.features.exercise.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.core.hardware.domain.CameraPhotoManager
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.CreateLocalExerciseUseCase
import com.alilopez.kt_demohilt.features.exercise.presentation.screens.AddExerciseUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddExerciseViewModel @Inject constructor(
    private val createLocalExerciseUseCase: CreateLocalExerciseUseCase,
    private val cameraPhotoManager: CameraPhotoManager,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExerciseUiState())
    val uiState: StateFlow<AddExerciseUiState> = _uiState.asStateFlow()

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

    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> = _photoUri.asStateFlow()

    private val _photoTaken = MutableStateFlow(false)
    val photoTaken: StateFlow<Boolean> = _photoTaken.asStateFlow()

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

    fun onPhotoTaken(success: Boolean) {
        _photoTaken.value = success
    }

    fun createPhotoUri(): Uri {
        val uri = cameraPhotoManager.createPhotoUri()
        _photoUri.value = uri
        return uri
    }

    fun onGalleryImageSelected(uri: Uri) {
        val file = cameraPhotoManager.copyGalleryImageToFile(uri)
        if (file != null) {
            _photoUri.value = uri
            _photoTaken.value = true
        }
    }

    fun hasCamera(): Boolean = cameraPhotoManager.hasCamera()

    fun createExercise() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val userId = sessionManager.currentUserId
                if (userId == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Usuario no autenticado"
                        )
                    }
                    return@launch
                }

                val imageFile = if (_photoTaken.value) cameraPhotoManager.getPhotoFile() else null

                createLocalExerciseUseCase(
                    name = _name.value,
                    description = _description.value,
                    userId = userId,
                    scheduledDays = _selectedDays.value,
                    exerciseType = _selectedExerciseType.value,
                    instructions = _instructions.value.ifBlank { null },
                    difficulty = _selectedDifficulty.value,
                    imageFile = imageFile
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        exerciseCreated = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al crear el ejercicio"
                    )
                }
            }
        }
    }

    fun resetExerciseCreated() {
        _uiState.update { it.copy(exerciseCreated = false) }
    }
}
