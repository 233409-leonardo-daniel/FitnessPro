package com.alilopez.kt_demohilt.features.recipies.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.core.hardware.domain.CameraPhotoManager
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.GetRecipesUseCase
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.UpdateRecipeUseCase
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.EditRecipeUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditRecipeViewModel @Inject constructor(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val updateRecipeUseCase: UpdateRecipeUseCase,
    private val cameraPhotoManager: CameraPhotoManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditRecipeUIState())
    val uiState: StateFlow<EditRecipeUIState> = _uiState.asStateFlow()

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

    private val _photoTaken = MutableStateFlow(false)
    val photoTaken: StateFlow<Boolean> = _photoTaken.asStateFlow()

    fun loadRecipe(recipeId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val recipies = getRecipesUseCase()
                val recipe = recipies.find { it.id == recipeId }

                _name.value = recipe?.name ?: ""
                _description.value = recipe?.description ?: ""
                _ingredients.value = recipe?.ingredients ?: ""
                _instructions.value = recipe?.instructions ?: ""
                _selectedMealType.value = recipe?.mealType
                _selectedDays.value = recipe?.scheduledDays ?: emptyList()
                _photoUri.value = null
                _photoTaken.value = false

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        recipe = recipe
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al obtener la receta"
                    )
                }
            }
        }
    }

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

    fun updateRecipe(recipeId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                updateRecipeUseCase(
                    recipeId = recipeId,
                    name = _name.value,
                    description = _description.value,
                    ingredients = _ingredients.value,
                    instructions = _instructions.value,
                    userId = _uiState.value.recipe?.userId,
                    scheduledDays = _selectedDays.value,
                    mealType = _selectedMealType.value,
                    imageUrl = if (_photoTaken.value) null else _uiState.value.recipe?.imageUrl,
                    imageFile = if (_photoTaken.value) cameraPhotoManager.getPhotoFile() else null
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        recipeUpdated = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al actualizar la receta"
                    )
                }
            }
        }
    }

    fun resetRecipeUpdated() {
        _uiState.update { it.copy(recipeUpdated = false) }
    }
}



