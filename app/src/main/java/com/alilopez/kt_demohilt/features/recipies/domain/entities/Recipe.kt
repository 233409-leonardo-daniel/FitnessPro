package com.alilopez.kt_demohilt.features.recipies.domain.entities

data class Recipe(
    val id: Int,
    val name: String,
    val description: String,
    val ingredients: String,
    val instructions: String,
    val userId: Int?,
    val scheduledDatetime: String?
)
