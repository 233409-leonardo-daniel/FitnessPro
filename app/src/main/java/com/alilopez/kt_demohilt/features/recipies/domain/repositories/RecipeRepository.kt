package com.alilopez.kt_demohilt.features.recipies.domain.repositories

import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe

interface RecipeRepository {
    suspend fun getRecipies(): List<Recipe>

    suspend fun createRecipe(
        name: String,
        description: String,
        ingredients: String,
        instructions: String,
        userId: Int?,
        scheduledDatetime: String?
    ): Recipe

    suspend fun updateRecipe(
        recipeId: Int,
        name: String,
        description: String,
        ingredients: String,
        instructions: String,
        userId: Int?,
        scheduledDatetime: String?
    ): Recipe

    suspend fun deleteRecipe(recipeId: Int)
}
