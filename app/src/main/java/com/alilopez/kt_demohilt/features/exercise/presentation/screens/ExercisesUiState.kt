package com.alilopez.kt_demohilt.features.exercise.presentation.screens

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise

data class ExercisesUiState(
    val isLoading: Boolean = false,
    val exercises: List<Exercise> = emptyList(),
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val isFilterExpanded: Boolean = false,
    val selectedBodyPart: String? = null
)
