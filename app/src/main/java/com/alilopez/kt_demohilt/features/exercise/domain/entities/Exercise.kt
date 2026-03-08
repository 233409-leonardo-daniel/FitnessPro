package com.alilopez.kt_demohilt.features.exercise.domain.entities

data class Exercise(
    val exerciseId: String,
    val name: String,
    val gifUrl: String,
    val targetMuscles: List<String>,
    val instructions: List<String>
)
