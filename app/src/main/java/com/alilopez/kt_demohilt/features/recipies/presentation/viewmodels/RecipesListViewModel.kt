package com.alilopez.kt_demohilt.features.recipies.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.DeleteRecipeUseCase
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.GetCommunityRecipesUseCase
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.GetRecipesUseCase
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.GetRemoteRecipesUseCase
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.GetUserRecipesUseCase
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.SearchRecipesByNameUseCase
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.RecipesListUIState
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
    private val getUserRecipesUseCase: GetUserRecipesUseCase,
    private val getCommunityRecipesUseCase: GetCommunityRecipesUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
    private val searchRecipesByNameUseCase: SearchRecipesByNameUseCase,
    private val getRemoteRecipesUseCase: GetRemoteRecipesUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipesListUIState())
    val uiState: StateFlow<RecipesListUIState> = _uiState.asStateFlow()

    val currentUserId: Int? get() = sessionManager.currentUserId

    init {
        loadUserRecipes()
    }

    fun loadRemoteRecipes() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val recipes = getRemoteRecipesUseCase()
                _uiState.update { currentState ->
                    currentState.copy(isLoading = false, remoteRecipes = recipes)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun loadCommunityRecipes() {
        _uiState.update { it.copy(isLoading = true) }

        currentUserId?.let { userId ->
            viewModelScope.launch {
                try {
                    val recipes = getCommunityRecipesUseCase(userId)
                    _uiState.update { currentState ->
                        currentState.copy(isLoading = false, communityRecipes = recipes)
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            }
        } ?: run {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Usuario no autenticado") }
        }
    }

    fun loadUserRecipes() {
        val userId = sessionManager.currentUserId
        if (userId == null) {
            _uiState.update { it.copy(localRecipes = emptyList(), errorMessage = "Usuario no autenticado") }
            return
        }

        viewModelScope.launch {
            try {
                val recipes = getUserRecipesUseCase(userId)
                _uiState.update { currentState ->
                    currentState.copy(localRecipes = recipes)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(localRecipes = emptyList(), errorMessage = e.message)
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun searchRecipes() {
        viewModelScope.launch {
            try {
                val recipes = searchRecipesByNameUseCase(_uiState.value.searchQuery.trim())
                _uiState.update { currentState ->
                    currentState.copy(localRecipes = recipes)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(localRecipes = emptyList(), errorMessage = e.message)
                }
            }
        }
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "", isSearchActive = false) }
    }

    fun deleteRecipe(recipeId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                deleteRecipeUseCase(recipeId)

                val updatedLocalRecipes = _uiState.value.localRecipes.filter { it.id != recipeId }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        localRecipes = updatedLocalRecipes,
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
