package com.alilopez.kt_demohilt.features.recipies.presentation.screens

import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe

data class RecipiesUIState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val recipies: List<Recipe> = emptyList(),
    val recipeCreated: Boolean = false,
    val recipeUpdated: Boolean = false,
    val recipeDeleted: Boolean = false
)
