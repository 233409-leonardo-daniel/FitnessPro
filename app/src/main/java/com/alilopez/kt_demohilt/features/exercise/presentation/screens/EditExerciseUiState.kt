package com.alilopez.kt_demohilt.features.exercise.presentation.screens

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise

data class EditExerciseUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val exercise: Exercise? = null,
    val exerciseUpdated: Boolean = false
)

