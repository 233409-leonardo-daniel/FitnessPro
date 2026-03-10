package com.alilopez.kt_demohilt.features.workoutplans.presentation.screens

import com.alilopez.kt_demohilt.features.workoutplans.domain.entities.WorkoutPlan

data class WorkoutPlansUIState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val workoutPlans: List<WorkoutPlan> = emptyList(),
    val planCreated: Boolean = false,
    val planDeleted: Boolean = false
)
