package com.alilopez.kt_demohilt.features.exercise.presentation.screens

data class AddExerciseUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val exerciseCreated: Boolean = false
)

