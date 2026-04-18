package com.alilopez.kt_demohilt.features.recipeplans.domain.entities

import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe

data class RecipePlan(
    val id: Int,
    val name: String,
    val description: String,
    val userId: Int,
    val isPrivate: Boolean,
    val recipes: List<Recipe> = emptyList(),
    val isDownloaded: Boolean = false
)
