package com.alilopez.kt_demohilt.features.exercise.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.features.exercise.domain.entities.ExerciseFilter
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.DeleteExerciseUseCase
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.DownloadExerciseUseCase
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetCommunityExercisesUseCase
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetExercisesByBodyPartUseCase
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetExercisesByUserIdUseCase
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetExercisesUseCase
import com.alilopez.kt_demohilt.features.exercise.presentation.screens.ExercisesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    private val deleteExerciseUseCase: DeleteExerciseUseCase,
    private val downloadExerciseUseCase: DownloadExerciseUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {
    private companion object {
        const val MIN_PULL_REFRESH_DURATION_MS = 1200L
    }

    private val _uiState = MutableStateFlow(ExercisesUiState())
    val uiState = _uiState.asStateFlow()
    val currentUserId: Int? get() = sessionManager.currentUserId

    init {
        loadUserExercises()
    }

    fun loadRemoteExercises(isUserRefresh: Boolean = false) {
        // En refresh, resetear offset y lista
        if (isUserRefresh) {
            _uiState.update {
                it.copy(
                    exercises = emptyList(),
                    nextOffset = 0,
                    hasNextPage = false
                )
            }
        }
        
        _uiState.update {
            it.copy(
                isLoading = true,
                isRefreshing = isUserRefresh
            )
        }

        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val result = getExercisesUseCase(offset = null)
            applyRefreshDelayIfNeeded(isUserRefresh, startTime)
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { paginated ->
                        currentState.copy(
                            isLoading = false,
                            isRefreshing = false,
                            exercises = paginated.exercises,
                            hasNextPage = paginated.hasNextPage,
                            nextOffset = paginated.nextCursor?.toIntOrNull() ?: 25,
                            totalRemoteExercises = paginated.total
                        )
                    },
                    onFailure = { error ->
                        currentState.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = error.message
                        )
                    }
                )
            }
        }
    }

    fun loadMoreRemoteExercises() {
        val currentState = _uiState.value
        if (!currentState.hasNextPage || currentState.isLoadingMore || currentState.isLoading) return

        _uiState.update { it.copy(isLoadingMore = true) }

        viewModelScope.launch {
            val result = if (currentState.isFiltered && currentState.selectedBodyPart != null) {
                getExercisesByBodyPartUseCase(
                    bodyPart = currentState.selectedBodyPart,
                    offset = currentState.nextOffset
                )
            } else {
                getExercisesUseCase(offset = currentState.nextOffset)
            }

            _uiState.update { state ->
                result.fold(
                    onSuccess = { paginated ->
                        state.copy(
                            isLoadingMore = false,
                            exercises = state.exercises + paginated.exercises,
                            hasNextPage = paginated.hasNextPage,
                            nextOffset = paginated.nextCursor?.toIntOrNull() ?: (state.nextOffset + 25),
                            totalRemoteExercises = paginated.total
                        )
                    },
                    onFailure = { error ->
                        state.copy(
                            isLoadingMore = false,
                            error = error.message
                        )
                    }
                )
            }
        }
    }

    fun loadCommunityExercises(isUserRefresh: Boolean = false) {
        _uiState.update {
            it.copy(
                isLoading = true,
                isRefreshing = isUserRefresh
            )
        }

        currentUserId?.let { userId ->
            viewModelScope.launch {
                val startTime = System.currentTimeMillis()
                val result = getCommunityExercisesUseCase(userId)
                applyRefreshDelayIfNeeded(isUserRefresh, startTime)
                _uiState.update { currentState ->
                    result.fold(
                        onSuccess = {
                            list -> currentState.copy(
                                isLoading = false,
                                isRefreshing = false,
                                communityExercises = list
                            )
                        },
                        onFailure = {
                            error -> currentState.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = error.message
                            )
                        }
                    )
                }
            }
        } ?: run {
            viewModelScope.launch {
                applyRefreshDelayIfNeeded(isUserRefresh, System.currentTimeMillis())
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = "Usuario no autenticado"
                    )
                }
            }
        }
    }

    fun loadUserExercises(isUserRefresh: Boolean = false) {
        val startTime = System.currentTimeMillis()
        val userId = sessionManager.currentUserId
        if (userId == null) {
            viewModelScope.launch {
                applyRefreshDelayIfNeeded(isUserRefresh, startTime)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        localExercises = emptyList(),
                        error = "Usuario no autenticado"
                    )
                }
            }
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                error = null,
                isRefreshing = isUserRefresh
            )
        }

        viewModelScope.launch {
            val result = getExercisesByUserIdUseCase(userId)
            applyRefreshDelayIfNeeded(isUserRefresh, startTime)
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = {
                        list -> currentState.copy(
                            isLoading = false,
                            isRefreshing = false,
                            localExercises = list
                        )
                    },
                    onFailure = { error ->
                        currentState.copy(
                            isLoading = false,
                            isRefreshing = false,
                            localExercises = emptyList(),
                            error = error.message
                        )
                    }
                )
            }
        }
    }

    private fun loadExercisesByBodyPart(bodyPart: String) {
        _uiState.update { 
            it.copy(
                isLoading = true,
                exercises = emptyList(),
                nextOffset = 0,
                hasNextPage = false
            )
        }

        viewModelScope.launch {
            val result = getExercisesByBodyPartUseCase(bodyPart = bodyPart, offset = null)
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { paginated ->
                        currentState.copy(
                            isLoading = false,
                            exercises = paginated.exercises,
                            hasNextPage = paginated.hasNextPage,
                            nextOffset = paginated.nextCursor?.toIntOrNull() ?: 25,
                        )
                    },
                    onFailure = { error ->
                        currentState.copy(isLoading = false, error = error.message)
                    }
                )
            }
        }
    }

    private suspend fun applyRefreshDelayIfNeeded(isUserRefresh: Boolean, startTime: Long) {
        if (!isUserRefresh) return
        val elapsed = System.currentTimeMillis() - startTime
        if (elapsed < MIN_PULL_REFRESH_DURATION_MS) {
            delay(MIN_PULL_REFRESH_DURATION_MS - elapsed)
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
        _uiState.update { 
            it.copy(
                selectedBodyPart = null, 
                isFiltered = false,
                nextOffset = 0,
                hasNextPage = false
            )
        }
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

    fun deleteExercise(exerciseId: Int) {
        viewModelScope.launch {
            try {
                deleteExerciseUseCase(exerciseId)
                _uiState.update { state ->
                    state.copy(
                        exerciseDeleted = true,
                        localExercises = state.localExercises.filter { it.id != exerciseId },
                        communityExercises = state.communityExercises.filter { it.id != exerciseId }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun resetExerciseDeleted() {
        _uiState.update { it.copy(exerciseDeleted = false) }
    }

    fun downloadExercise(exerciseId: Int, exerciseName: String) {
        downloadExerciseUseCase(exerciseId, exerciseName)
    }
}
