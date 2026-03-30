package com.alilopez.kt_demohilt.features.user.data.datasources.remote.model

import com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.model.LocalExerciseDto
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.model.RecipeDto
import com.google.gson.annotations.SerializedName

data class UserDailyResponse(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("user_lastname")
    val userLastname: String,
    val day: String,
    val timezone: String,
    val exercises: List<LocalExerciseDto>,
    val recipes: List<RecipeDto>
)