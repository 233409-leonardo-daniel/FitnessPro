package com.alilopez.kt_demohilt.features.recipeplans.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.GetRecipiesUseCase
import com.alilopez.kt_demohilt.features.recipeplans.domain.usecases.AddRecipeToPlanUseCase
import com.alilopez.kt_demohilt.features.recipeplans.domain.usecases.GetPlanRecipesUseCase
import com.alilopez.kt_demohilt.features.recipeplans.domain.usecases.RemoveRecipeFromPlanUseCase
import com.alilopez.kt_demohilt.features.recipeplans.presentation.screens.RecipePlanDetailUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipePlanDetailViewModel @Inject constructor(
    private val getPlanRecipesUseCase: GetPlanRecipesUseCase,
    private val getRecipiesUseCase: GetRecipiesUseCase,
    private val addRecipeToPlanUseCase: AddRecipeToPlanUseCase,
    private val removeRecipeFromPlanUseCase: RemoveRecipeFromPlanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipePlanDetailUIState())
    val uiState: StateFlow<RecipePlanDetailUIState> = _uiState.asStateFlow()

    fun loadPlanRecipes(planId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getPlanRecipesUseCase(planId).fold(
                onSuccess = { recipes ->
                    _uiState.update { it.copy(isLoading = false, recipes = recipes) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun loadAvailableRecipes() {
        viewModelScope.launch {
            try {
                val allRecipes = getRecipiesUseCase()
                val currentIds = _uiState.value.recipes.map { it.id }
                val available = allRecipes.filter { it.id !in currentIds }
                _uiState.update { it.copy(availableRecipes = available, isAddingRecipe = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Error al cargar recetas") }
            }
        }
    }

    fun addRecipeToPlan(planId: Int, recipeId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            addRecipeToPlanUseCase(planId, recipeId).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, recipeAdded = true, isAddingRecipe = false) }
                    loadPlanRecipes(planId)
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun removeRecipeFromPlan(planId: Int, recipeId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            removeRecipeFromPlanUseCase(planId, recipeId).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, recipeRemoved = true) }
                    loadPlanRecipes(planId)
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun closeAddRecipe() {
        _uiState.update { it.copy(isAddingRecipe = false) }
    }

    fun resetStatus() {
        _uiState.update { it.copy(recipeAdded = false, recipeRemoved = false) }
    }
}
