package com.alilopez.kt_demohilt.features.workoutplans.domain.entities

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise

data class WorkoutPlan(
    val id: Int,
    val name: String,
    val description: String,
    val userId: Int,
    val planType: String,
    val isPrivate: Boolean,
    val exercises: List<Exercise> = emptyList(),
    val isDownloaded: Boolean = false
)
