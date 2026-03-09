package com.alilopez.kt_demohilt.features.recipies.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.CreateRecipeUseCase
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.DeleteRecipeUseCase
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.GetRecipiesUseCase
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.UpdateRecipeUseCase
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.RecipiesUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipiesViewModel @Inject constructor(
    private val getRecipiesUseCase: GetRecipiesUseCase,
    private val createRecipeUseCase: CreateRecipeUseCase,
    private val updateRecipeUseCase: UpdateRecipeUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipiesUIState())
    val uiState: StateFlow<RecipiesUIState> = _uiState.asStateFlow()

    fun getRecipies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val recipies = getRecipiesUseCase()
                _uiState.update { it.copy(isLoading = false, recipies = recipies) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al obtener las recetas"
                    )
                }
            }
        }
    }

    fun createRecipe(
        name: String,
        description: String,
        ingredients: String,
        instructions: String,
        userId: Int? = null,
        scheduledDays: List<String> = emptyList(),
        mealType: String? = null,
        imageUrl: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val newRecipe = createRecipeUseCase(
                    name = name,
                    description = description,
                    ingredients = ingredients,
                    instructions = instructions,
                    userId = userId,
                    scheduledDays = scheduledDays,
                    mealType = mealType,
                    imageUrl = imageUrl
                )

                // Actualizar la lista de recetas añadiendo la nueva receta
                val updatedRecipes = _uiState.value.recipies + newRecipe
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        recipies = updatedRecipes,
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

    fun updateRecipe(
        recipeId: Int,
        name: String,
        description: String,
        ingredients: String,
        instructions: String,
        userId: Int? = null,
        scheduledDays: List<String> = emptyList(),
        mealType: String? = null,
        imageUrl: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val updatedRecipe = updateRecipeUseCase(
                    recipeId = recipeId,
                    name = name,
                    description = description,
                    ingredients = ingredients,
                    instructions = instructions,
                    userId = userId,
                    scheduledDays = scheduledDays,
                    mealType = mealType,
                    imageUrl = imageUrl
                )

                // Actualizar la lista de recetas reemplazando la receta actualizada
                val updatedRecipes = _uiState.value.recipies.map { recipe ->
                    if (recipe.id == recipeId) updatedRecipe else recipe
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        recipies = updatedRecipes,
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

    fun deleteRecipe(recipeId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                deleteRecipeUseCase(recipeId)

                // Actualizar la lista de recetas eliminando la receta
                val updatedRecipes = _uiState.value.recipies.filter { it.id != recipeId }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        recipies = updatedRecipes,
                        recipeDeleted = true
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al eliminar la receta"
                    )
                }
            }
        }
    }

    fun resetRecipeCreated() {
        _uiState.update { it.copy(recipeCreated = false) }
    }

    fun resetRecipeUpdated() {
        _uiState.update { it.copy(recipeUpdated = false) }
    }

    fun resetRecipeDeleted() {
        _uiState.update { it.copy(recipeDeleted = false) }
    }
}
