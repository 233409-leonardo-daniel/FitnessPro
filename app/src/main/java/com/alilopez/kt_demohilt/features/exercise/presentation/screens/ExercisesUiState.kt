package com.alilopez.kt_demohilt.features.exercise.presentation.screens

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise

data class ExercisesUiState(
    val isLoading: Boolean = false,
    val exercises: List<Exercise> = emptyList(),
    val communityExercises: List<Exercise> = emptyList(),
    val localExercises: List<Exercise> = emptyList(),
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val isFilterExpanded: Boolean = false,
    val selectedBodyPart: String? = null,
    val isSyncing: Boolean = false,
    val isFiltered: Boolean = false,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val exerciseDeleted: Boolean = false,
    // Paginación - ejercicios remotos (offset/limit)
    val hasNextPage: Boolean = false,
    val nextOffset: Int = 0,
    val isLoadingMore: Boolean = false,
    val totalRemoteExercises: Int = 0
)
