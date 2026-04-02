package com.alilopez.kt_demohilt.features.progression.domain.entities

data class ProgressionSummary(
    val userId: Int,
    val targetWeight: Float,
    val initialWeight: Float,
    val currentWeight: Float,
    val weightChange: Float,
    val entriesCount: Int,
    val onTrack: Boolean,
    val trend: String
)
