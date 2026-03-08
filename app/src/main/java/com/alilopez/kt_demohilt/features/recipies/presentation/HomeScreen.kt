package com.alilopez.kt_demohilt.features.recipies.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.RecipesScreen

@Composable
fun HomeScreen(
    onNavigateToAddRecipe: () -> Unit,
    onNavigateToEditRecipe: (Int) -> Unit,
    onNavigateToExercises: () -> Unit
) {
    RecipesScreen(
        onNavigateToAddRecipe = onNavigateToAddRecipe,
        onNavigateToEditRecipe = onNavigateToEditRecipe,
        onNavigateToExercises = onNavigateToExercises,
        modifier = Modifier.fillMaxSize()
    )
}
