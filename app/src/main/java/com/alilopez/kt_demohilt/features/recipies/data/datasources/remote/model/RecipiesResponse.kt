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
    @SerializedName("scheduled_days")
    val scheduledDays: List<String>,
    @SerializedName("meal_type")
    val mealType: String?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("audio_url")
    val audioUrl: String?
)

data class RecipeCreateDto(
    val name: String,
    val description: String,
    val ingredients: String,
    val instructions: String,
    @SerializedName("user_id")
    val userId: Int?,
    @SerializedName("scheduled_days")
    val scheduledDays: List<String> = emptyList(),
    @SerializedName("meal_type")
    val mealType: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("audio_url")
    val audioUrl: String? = null
)

data class RemoteRecipeDto(
    val id: String,
    val name: String,
    val category: String? = null,
    val area: String? = null,
    val instructions: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("youtube_url")
    val youtubeUrl: String? = null,
    @SerializedName("source_url")
    val sourceUrl: String? = null,
    val tags: List<String> = emptyList(),
    val ingredients: List<String> = emptyList(),
    val measures: List<String> = emptyList()
)

data class RemoteRecipesResponse(
    @SerializedName("recipes")
    val recipes: List<RemoteRecipeDto> = emptyList()
)

