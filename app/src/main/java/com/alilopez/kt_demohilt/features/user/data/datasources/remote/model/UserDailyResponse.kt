package com.alilopez.kt_demohilt.features.user.data.datasources.remote.model

import com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.model.ExercisesDto
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.model.RecipeDto

data class UserDailyResponse(
    val user_id: Int,
    val user_name: String,
    val user_lastname: String,
    val day: String,
    val timezone: String,
    val exercises: List<ExercisesDto>,
    val recipes: List<RecipeDto>
)