package com.alilopez.kt_demohilt.features.exercise.domain.entities

data class Exercise(
    val exerciseId: String? = null,
    val id: Int? = null,
    val name: String,
    val gifUrl: String,
    val targetMuscles: List<String>,
    val instructions: List<String>,
    val description: String? = null,
    val userId: Int? = null,
    val scheduledDays: List<String> = emptyList(),
    val bodyparts: List<String> = emptyList(),
    val equipments: List<String> = emptyList(),
    val secondaryMuscles: List<String> = emptyList(),
    val exerciseType: String? = null,
    val difficulty: String? = null,
    val private: Boolean = false
)
