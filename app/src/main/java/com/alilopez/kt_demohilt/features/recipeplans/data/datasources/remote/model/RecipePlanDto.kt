package com.alilopez.kt_demohilt.features.recipeplans.data.datasources.remote.model

import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.model.RecipeDto
import com.google.gson.annotations.SerializedName

data class RecipePlanDto(
    val id: Int,
    val name: String,
    val description: String,
    @SerializedName("user_id")
    val userId: Int,
    val private: Boolean,
    val recipes: List<RecipeDto> = emptyList()
)

data class RecipePlanCreateDto(
    val name: String,
    val description: String,
    @SerializedName("user_id")
    val userId: Int,
    val private: Boolean = true
)

data class AddRecipeToPlanDto(
    @SerializedName("recipe_id")
    val recipeId: Int
)
