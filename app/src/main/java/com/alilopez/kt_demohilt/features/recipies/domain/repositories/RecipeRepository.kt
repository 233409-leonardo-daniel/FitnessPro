package com.alilopez.kt_demohilt.features.recipies.domain.repositories

import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe
import java.io.File

interface RecipeRepository {
    suspend fun getRecipes(): List<Recipe>
    suspend fun searchRecipesByName(name: String): List<Recipe>
    suspend fun getRecipeDetail(recipeId: Int): Recipe
    suspend fun createRecipe(
        name: String,
        description: String,
        ingredients: String,
        instructions: String,
        userId: Int?,
        scheduledDays: List<String>,
        mealType: String?,
        imageFile: File?,
        audioFile: File?
    ): Recipe
    suspend fun updateRecipe(
        recipeId: Int,
        name: String,
        description: String,
        ingredients: String,
        instructions: String,
        userId: Int?,
        scheduledDays: List<String>,
        mealType: String?,
        imageUrl: String?
    ): Recipe
    suspend fun deleteRecipe(recipeId: Int)
    suspend fun getCommunityRecipes(userId: Int): List<Recipe>
    suspend fun getUserRecipes(userId: Int): List<Recipe>
}
