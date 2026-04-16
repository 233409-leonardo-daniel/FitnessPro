package com.alilopez.kt_demohilt.features.downloads.presentation.screens

import com.alilopez.kt_demohilt.features.recipeplans.domain.entities.RecipePlan
import com.alilopez.kt_demohilt.features.workoutplans.domain.entities.WorkoutPlan

data class DownloadLibraryUIState(
    val isLoading: Boolean = false,
    val downloadedWorkouts: List<WorkoutPlan> = emptyList(),
    val downloadedRecipes: List<RecipePlan> = emptyList(),
    val errorMessage: String? = null
)
