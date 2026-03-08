package com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class RecipiesResponse(
    val data: List<RecipeDto>
)

data class RecipeDto(
    val id: Int,
    val name: String,
    val description: String,
    val ingredients: String,
    val instructions: String,
    @SerializedName("user_id")
    val userId: Int?,
    @SerializedName("scheduled_datetime")
    val scheduledDatetime: String?
)

data class RecipeCreateDto(
    val name: String,
    val description: String,
    val ingredients: String,
    val instructions: String,
    @SerializedName("user_id")
    val userId: Int?,
    @SerializedName("scheduled_datetime")
    val scheduledDatetime: String? = null
)
