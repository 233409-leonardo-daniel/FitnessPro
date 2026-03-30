package com.alilopez.kt_demohilt.features.exercise.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.features.exercise.domain.entities.ExerciseFilter
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetCommunityExercisesUseCase
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetExercisesByBodyPartUseCase
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetExercisesUseCase
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetExercisesByUserIdUseCase
import com.alilopez.kt_demohilt.features.exercise.presentation.screens.ExercisesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val getExercisesUseCase: GetExercisesUseCase,
    private val getExercisesByBodyPartUseCase: GetExercisesByBodyPartUseCase,
    private val getExercisesByUserIdUseCase: GetExercisesByUserIdUseCase,
    private val getCommunityExercisesUseCase: GetCommunityExercisesUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExercisesUiState())
    val uiState = _uiState.asStateFlow()
    val currentUserId: Int? get() = sessionManager.currentUserId

    init {
        loadUserExercises()
    }

    fun loadRemoteExercises() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = getExercisesUseCase()
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { list -> currentState.copy(isLoading = false, exercises = list) },
                    onFailure = { error -> currentState.copy(isLoading = false, error = error.message) }
                )
            }
        }
    }

    fun loadCommunityExercises() {
        _uiState.update { it.copy(isLoading = true) }

        currentUserId?.let { userId ->
            viewModelScope.launch {
                val result = getCommunityExercisesUseCase(userId)
                _uiState.update { currentState ->
                    result.fold(
                        onSuccess = { list -> currentState.copy(isLoading = false, communityExercises = list) },
                        onFailure = { error -> currentState.copy(isLoading = false, error = error.message) }
                    )
                }
            }
        } ?: run {
            _uiState.update { it.copy(isLoading = false, error = "Usuario no autenticado") }
        }
    }

    fun loadUserExercises() {
        val userId = sessionManager.currentUserId
        if (userId == null) {
            _uiState.update { it.copy(localExercises = emptyList(), error = "Usuario no autenticado") }
            return
        }

        viewModelScope.launch {
            val result = getExercisesByUserIdUseCase(userId)
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { list -> currentState.copy(localExercises = list) },
                    onFailure = { error ->
                        currentState.copy(localExercises = emptyList(), error = error.message)
                    }
                )
            }
        }
    }

    private fun loadExercisesByBodyPart(bodyPart: String) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = getExercisesByBodyPartUseCase(bodyPart)
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { list ->
                        currentState.copy(isLoading = false, exercises = list)
                    },
                    onFailure = { error ->
                        currentState.copy(isLoading = false, error = error.message)
                    }
                )
            }
        }
    }

    fun toggleFilter() {
        _uiState.update { it.copy(isFilterExpanded = !it.isFilterExpanded) }
    }

    fun onBodyPartChecked(bodyPart: String, isChecked: Boolean) {
        _uiState.update {
            it.copy(selectedBodyPart = if (isChecked) bodyPart else null)
        }
    }

    fun applyFilters() {
        val filter = ExerciseFilter(
            bodyPart = _uiState.value.selectedBodyPart
            // difficulty = _uiState.value.selectedDifficulty,
            // exerciseType = _uiState.value.selectedExerciseType
        )
        _uiState.update { it.copy(isFilterExpanded = false) }

        if (!filter.isEmpty) {
            _uiState.update { it.copy(isFiltered = true, exercises = emptyList()) }
            filter.bodyPart?.let { bodyPart ->
                loadExercisesByBodyPart(bodyPart)
            }
        } else {
            _uiState.update { it.copy(isFiltered = false) }
            loadRemoteExercises()
        }
    }

    fun clearFilters() {
        _uiState.update { it.copy(selectedBodyPart = null, isFiltered = false) }
        loadRemoteExercises()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                isSearchActive = if (query.isBlank()) false else it.isSearchActive
            )
        }
    }

    fun searchExercises() {
        _uiState.update {
            it.copy(isSearchActive = it.searchQuery.trim().isNotBlank())
        }
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "", isSearchActive = false) }
    }
}
