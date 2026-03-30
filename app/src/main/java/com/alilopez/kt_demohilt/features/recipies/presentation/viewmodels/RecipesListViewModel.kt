package com.alilopez.kt_demohilt.features.recipies.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.DeleteRecipeUseCase
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.GetRecipesUseCase
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.SearchRecipesByNameUseCase
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.RecipesListUIState
import com.alilopez.kt_demohilt.core.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesListViewModel @Inject constructor(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
    private val searchRecipesByNameUseCase: SearchRecipesByNameUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipesListUIState())
    val uiState: StateFlow<RecipesListUIState> = _uiState.asStateFlow()

    val currentUserId: Int? get() = sessionManager.currentUserId

    fun getRecipies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val recipies = getRecipesUseCase()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        recipies = recipies,
                        isSearchActive = false
                    )
                }
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

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun searchRecipes() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isBlank()) {
            getRecipies()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val recipies = searchRecipesByNameUseCase(query)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        recipies = recipies,
                        isSearchActive = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al buscar recetas"
                    )
                }
            }
        }
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "", isSearchActive = false) }
        getRecipies()
    }

    fun deleteRecipe(recipeId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                deleteRecipeUseCase(recipeId)

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

    fun resetRecipeDeleted() {
        _uiState.update { it.copy(recipeDeleted = false) }
    }
}
