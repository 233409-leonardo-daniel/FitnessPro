package com.alilopez.kt_demohilt.features.workoutplans.data.datasources.remote.model

import com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.model.LocalExerciseDto
import com.google.gson.annotations.SerializedName

data class WorkoutPlanDto(
    val id: Int,
    val name: String,
    val description: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("plan_type")
    val planType: String,
    val private: Boolean,
    val exercises: List<LocalExerciseDto> = emptyList()
)

data class WorkoutPlanCreateDto(
    val name: String,
    val description: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("plan_type")
    val planType: String = "PERSONAL",
    val private: Boolean = true
)

data class AddExerciseToPlanDto(
    @SerializedName("exercise_id")
    val exerciseId: Int
)
