package com.alilopez.kt_demohilt.features.progression.domain.entities

data class ProgressionEntry(
    val id: Int,
    val userId: Int,
    val weight: Float,
    val date: String
)
