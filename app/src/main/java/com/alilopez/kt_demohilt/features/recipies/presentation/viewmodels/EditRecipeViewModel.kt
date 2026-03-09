package com.alilopez.kt_demohilt.features.recipies.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.GetRecipiesUseCase
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
    private val getRecipiesUseCase: GetRecipiesUseCase,
    private val updateRecipeUseCase: UpdateRecipeUseCase
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

    fun loadRecipe(recipeId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val recipies = getRecipiesUseCase()
                val recipe = recipies.find { it.id == recipeId }

                _name.value = recipe?.name ?: ""
                _description.value = recipe?.description ?: ""
                _ingredients.value = recipe?.ingredients ?: ""
                _instructions.value = recipe?.instructions ?: ""
                _selectedMealType.value = recipe?.mealType
                _selectedDays.value = recipe?.scheduledDays ?: emptyList()

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
                    imageUrl = _uiState.value.recipe?.imageUrl
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



