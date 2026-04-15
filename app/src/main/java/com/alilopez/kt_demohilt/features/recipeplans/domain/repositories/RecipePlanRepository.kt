package com.alilopez.kt_demohilt.features.recipeplans.domain.repositories

import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe
import com.alilopez.kt_demohilt.features.recipeplans.domain.entities.RecipePlan
import kotlinx.coroutines.flow.Flow

interface RecipePlanRepository {
    suspend fun getUserRecipePlans(userId: Int): List<RecipePlan>
    suspend fun createRecipePlan(name: String, description: String, userId: Int, isPrivate: Boolean): RecipePlan
    suspend fun addRecipeToPlan(planId: Int, recipeId: Int): RecipePlan
    suspend fun getPlanRecipes(planId: Int): List<Recipe>
    suspend fun deleteRecipePlan(planId: Int)
    suspend fun removeRecipeFromPlan(planId: Int, recipeId: Int): RecipePlan
    
    // Para modo offline
    fun getDownloadedRecipePlans(): Flow<List<RecipePlan>>
    suspend fun getDownloadedPlanIds(): List<Int>
}
