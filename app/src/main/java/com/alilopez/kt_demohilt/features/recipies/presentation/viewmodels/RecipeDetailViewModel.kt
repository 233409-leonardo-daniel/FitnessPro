package com.alilopez.kt_demohilt.features.recipies.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.GetRecipeDetailUseCase
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.RecipeDetailUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUIState())
    val uiState: StateFlow<RecipeDetailUIState> = _uiState.asStateFlow()

    fun loadRecipe(recipeId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val recipe = getRecipeDetailUseCase(recipeId)
                _uiState.update { it.copy(isLoading = false, recipe = recipe) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error desconocido") }
            }
        }
    }
}



