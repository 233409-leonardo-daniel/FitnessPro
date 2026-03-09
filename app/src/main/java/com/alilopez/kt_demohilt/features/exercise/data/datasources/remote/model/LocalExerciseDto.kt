package com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class LocalExerciseDto(
    val id: Int,
    val name: String,
    val description: String? = null,
    @SerializedName("user_id")
    val userId: Int? = null,
    @SerializedName("scheduled_days")
    val scheduledDays: List<String> = emptyList(),
    @SerializedName("image_url")
    val imageUrl: String? = null,
    val bodyparts: List<String> = emptyList(),
    val equipments: List<String> = emptyList(),
    val targetMuscles: List<String> = emptyList(),
    val secondaryMuscles: List<String> = emptyList(),
    @SerializedName("exercise_type")
    val exerciseType: String? = null,
    val instructions: String? = null,
    val difficulty: String? = null
)

