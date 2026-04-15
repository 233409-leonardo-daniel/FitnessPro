package com.alilopez.kt_demohilt.features.recipeplans.data.repositories

import com.alilopez.kt_demohilt.core.database.dao.RecipePlanDao
import com.alilopez.kt_demohilt.core.network.FitnessProApi
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe
import com.alilopez.kt_demohilt.features.recipeplans.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.recipeplans.data.datasources.remote.model.AddRecipeToPlanDto
import com.alilopez.kt_demohilt.features.recipeplans.data.datasources.remote.model.RecipePlanCreateDto
import com.alilopez.kt_demohilt.features.recipeplans.domain.entities.RecipePlan
import com.alilopez.kt_demohilt.features.recipeplans.domain.repositories.RecipePlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecipePlanRepositoryImpl @Inject constructor(
    private val api: FitnessProApi,
    private val recipePlanDao: RecipePlanDao
) : RecipePlanRepository {

    override suspend fun getUserRecipePlans(userId: Int): List<RecipePlan> {
        val remotePlans = api.getUserRecipePlans(userId).map { it.toDomain() }
        val downloadedIds = recipePlanDao.getDownloadedPlanIds().toSet()
        
        return remotePlans.map { plan ->
            if (downloadedIds.contains(plan.id)) {
                plan.copy(isDownloaded = true)
            } else {
                plan
            }
        }
    }

    override suspend fun createRecipePlan(
        name: String,
        description: String,
        userId: Int,
        isPrivate: Boolean
    ): RecipePlan {
        val createDto = RecipePlanCreateDto(
            name = name,
            description = description,
            userId = userId,
            private = isPrivate
        )
        return api.createRecipePlan(createDto).toDomain()
    }

    override suspend fun addRecipeToPlan(planId: Int, recipeId: Int): RecipePlan {
        val addDto = AddRecipeToPlanDto(recipeId = recipeId)
        return api.addRecipeToPlan(planId, addDto).toDomain()
    }

    override suspend fun getPlanRecipes(planId: Int): List<Recipe> {
        return api.getPlanRecipes(planId).map { it.toDomain() }
    }

    override suspend fun deleteRecipePlan(planId: Int) {
        api.deleteRecipePlan(planId)
        recipePlanDao.deleteRecipePlan(planId)
    }

    override suspend fun removeRecipeFromPlan(planId: Int, recipeId: Int): RecipePlan {
        return api.removeRecipeFromPlan(planId, recipeId).toDomain()
    }

    override fun getDownloadedRecipePlans(): Flow<List<RecipePlan>> {
        return recipePlanDao.getDownloadedRecipePlans().map { entities ->
            entities.map { it.toDomain().copy(isDownloaded = true) }
        }
    }

    override suspend fun getDownloadedPlanIds(): List<Int> {
        return recipePlanDao.getDownloadedPlanIds()
    }
}
