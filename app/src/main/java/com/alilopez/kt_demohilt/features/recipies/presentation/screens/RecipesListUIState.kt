package com.alilopez.kt_demohilt.features.recipies.presentation.screens

import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe

data class RecipesListUIState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val localRecipes: List<Recipe> = emptyList(),
    val communityRecipes: List<Recipe> = emptyList(),
    val remoteRecipes: List<Recipe> = emptyList(),
    val recipeDeleted: Boolean = false,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false
)

