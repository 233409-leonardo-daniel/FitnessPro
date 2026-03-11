package com.alilopez.kt_demohilt.features.recipeplans.presentation.screens

import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe

data class RecipePlanDetailUIState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val recipes: List<Recipe> = emptyList(),
    val availableRecipes: List<Recipe> = emptyList(),
    val isAddingRecipe: Boolean = false,
    val recipeAdded: Boolean = false,
    val recipeRemoved: Boolean = false
)
