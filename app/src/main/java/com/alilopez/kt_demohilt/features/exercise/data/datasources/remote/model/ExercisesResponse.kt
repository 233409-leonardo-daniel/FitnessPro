package com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.model

data class ExercisesResponse(
    val data: List<ExercisesDto>
)

data class ExercisesDto(
    val exerciseId: String,
    val name: String,
    val gifUrl: String,
    val targetMuscles: List<String>,
    val instructions: List<String>
)
