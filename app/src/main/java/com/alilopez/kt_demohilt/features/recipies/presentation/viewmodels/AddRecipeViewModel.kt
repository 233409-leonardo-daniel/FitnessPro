package com.alilopez.kt_demohilt.features.recipies.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.core.hardware.domain.CameraPhotoManager
import com.alilopez.kt_demohilt.core.hardware.domain.MicrophoneManager
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.CreateRecipeUseCase
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.AddRecipeUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddRecipeViewModel @Inject constructor(
    private val createRecipeUseCase: CreateRecipeUseCase,
    private val cameraPhotoManager: CameraPhotoManager,
    private val microphoneManager: MicrophoneManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddRecipeUIState())
    val uiState: StateFlow<AddRecipeUIState> = _uiState.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _ingredients = MutableStateFlow("")
    val ingredients: StateFlow<String> = _ingredients.asStateFlow()

    private val _instructions = MutableStateFlow("")
    val instructions: StateFlow<String> = _instructions.asStateFlow()

    private val _selectedMealType = MutableStateFlow<String?>(null)
    val selectedMealType: StateFlow<String?> = _selectedMealType.asStateFlow()

    private val _selectedDays = MutableStateFlow<List<String>>(emptyList())
    val selectedDays: StateFlow<List<String>> = _selectedDays.asStateFlow()

    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> = _photoUri.asStateFlow()

    fun onNameChange(value: String) {
        _name.value = value
    }

    fun onDescriptionChange(value: String) {
        _description.value = value
    }

    fun onIngredientsChange(value: String) {
        _ingredients.value = value
    }

    fun onInstructionsChange(value: String) {
        _instructions.value = value
    }

    fun onMealTypeChange(mealType: String?) {
        _selectedMealType.value = mealType
    }

    fun onDayToggle(day: String) {
        _selectedDays.value = if (_selectedDays.value.contains(day)) {
            _selectedDays.value - day
        } else {
            _selectedDays.value + day
        }
    }

    fun onPhotoTaken(success: Boolean) {
        _uiState.update { it.copy(photoTaken = success) }
    }

    fun createPhotoUri(): Uri {
        val uri = cameraPhotoManager.createPhotoUri()
        _photoUri.value = uri
        return uri
    }

    fun hasCamera(): Boolean = cameraPhotoManager.hasCamera()

    // --- Audio recording ---

    fun hasMicrophone(): Boolean = microphoneManager.hasMicrophone()

    fun toggleRecording() {
        if (_uiState.value.isRecording) {
            microphoneManager.stopRecording()
            _uiState.update {
                it.copy(
                    isRecording = false,
                    audioRecorded = microphoneManager.getRecordingFile() != null
                )
            }
        } else {
            microphoneManager.startRecording()
            _uiState.update {
                it.copy(isRecording = true, audioRecorded = false)
            }
        }
    }

    fun deleteAudio() {
        microphoneManager.getRecordingFile()?.delete()
        _uiState.update { it.copy(audioRecorded = false) }
    }

    fun createRecipe() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val imageFile = if (_uiState.value.photoTaken) cameraPhotoManager.getPhotoFile() else null
                val audioFile = if (_uiState.value.audioRecorded) microphoneManager.getRecordingFile() else null

                createRecipeUseCase(
                    name = _name.value,
                    description = _description.value,
                    ingredients = _ingredients.value,
                    instructions = _instructions.value,
                    userId = 1,
                    scheduledDays = _selectedDays.value,
                    mealType = _selectedMealType.value,
                    imageFile = imageFile,
                    audioFile = audioFile
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        recipeCreated = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al crear la receta"
                    )
                }
            }
        }
    }

    fun resetRecipeCreated() {
        _uiState.update { it.copy(recipeCreated = false) }
    }

    override fun onCleared() {
        super.onCleared()
        // Detener grabación si el ViewModel se destruye
        if (_uiState.value.isRecording) {
            microphoneManager.stopRecording()
        }
    }
}



