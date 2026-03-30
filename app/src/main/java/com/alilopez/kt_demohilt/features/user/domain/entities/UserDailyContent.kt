package com.alilopez.kt_demohilt.features.user.domain.entities

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe

data class UserDailyContent(
    val userId: Int,
    val userName: String,
    val userLastname: String,
    val day: String,
    val timezone: String,
    val exercises: List<Exercise>,
    val recipes: List<Recipe>
)

