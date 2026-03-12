package com.alilopez.kt_demohilt.features.recipies.presentation.screens

data class AddRecipeUIState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val recipeCreated: Boolean = false,
    val photoTaken: Boolean = false,
    val isRecording: Boolean = false,
    val audioRecorded: Boolean = false
)

