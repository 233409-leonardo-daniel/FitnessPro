package com.alilopez.kt_demohilt.features.workoutplans.presentation.screens

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise

data class WorkoutDetailUIState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val exercises: List<Exercise> = emptyList(),
    val availableLocalExercises: List<Exercise> = emptyList(),
    val isAddingExercise: Boolean = false,
    val exerciseAdded: Boolean = false,
    val exerciseRemoved: Boolean = false
)
