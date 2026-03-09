package com.alilopez.kt_demohilt.features.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetExercisesUseCase
import com.alilopez.kt_demohilt.features.home.presentation.screens.HomeUIState
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.GetRecipiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRecipiesUseCase: GetRecipiesUseCase,
    private val getExercisesUseCase: GetExercisesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        loadRecipes()
        loadExercises()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            _uiState.update { it.copy(recipesLoading = true, recipesError = null) }
            try {
                val recipes = getRecipiesUseCase()
                _uiState.update { it.copy(recipesLoading = false, recipes = recipes) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(recipesLoading = false, recipesError = e.message ?: "Error al cargar recetas")
                }
            }
        }
    }

    private fun loadExercises() {
        viewModelScope.launch {
            _uiState.update { it.copy(exercisesLoading = true, exercisesError = null) }
            val result = getExercisesUseCase()
            result.fold(
                onSuccess = { exercises ->
                    _uiState.update { it.copy(exercisesLoading = false, exercises = exercises) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(exercisesLoading = false, exercisesError = error.message ?: "Error al cargar ejercicios")
                    }
                }
            )
        }
    }
}