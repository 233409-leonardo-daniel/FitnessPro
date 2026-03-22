package com.alilopez.kt_demohilt.features.recipies.presentation.screens

import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe

data class RecipeDetailUIState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val recipe: Recipe? = null
)


