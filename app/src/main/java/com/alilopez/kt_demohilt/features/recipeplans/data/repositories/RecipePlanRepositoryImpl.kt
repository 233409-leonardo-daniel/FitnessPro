package com.alilopez.kt_demohilt.features.recipeplans.data.repositories

import com.alilopez.kt_demohilt.core.database.dao.RecipePlanDao
import com.alilopez.kt_demohilt.core.network.FitnessProApi
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.mapper.toDomain as toRecipeDomainFromDto
import com.alilopez.kt_demohilt.features.recipies.data.datasources.local.mapper.toDomain as toRecipeDomainFromEntity
import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe
import com.alilopez.kt_demohilt.features.recipeplans.data.datasources.remote.mapper.toDomain as toPlanDomain
import com.alilopez.kt_demohilt.features.recipeplans.data.datasources.remote.model.AddRecipeToPlanDto
import com.alilopez.kt_demohilt.features.recipeplans.data.datasources.remote.model.RecipePlanCreateDto
import com.alilopez.kt_demohilt.features.recipeplans.domain.entities.RecipePlan
import com.alilopez.kt_demohilt.features.recipeplans.domain.repositories.RecipePlanRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class RecipePlanRepositoryImpl @Inject constructor(
    private val api: FitnessProApi,
    private val recipePlanDao: RecipePlanDao
) : RecipePlanRepository {

    override suspend fun getUserRecipePlans(userId: Int): List<RecipePlan> {
        return try {
            val remotePlans = api.getUserRecipePlans(userId).map { it.toPlanDomain() }
            val downloadedIds = recipePlanDao.getDownloadedPlanIds().toSet()
            
            remotePlans.map { plan ->
                if (downloadedIds.contains(plan.id)) {
                    plan.copy(isDownloaded = true)
                } else {
                    plan
                }
            }
        } catch (e: Exception) {
            recipePlanDao.getDownloadedRecipePlans().first().map { 
                it.toPlanDomain().copy(isDownloaded = true) 
            }
        }
    }

    override suspend fun createRecipePlan(name: String, description: String, userId: Int, isPrivate: Boolean): RecipePlan {
        val createDto = RecipePlanCreateDto(name = name, description = description, userId = userId, private = isPrivate)
        return api.createRecipePlan(createDto).toPlanDomain()
    }

    override suspend fun addRecipeToPlan(planId: Int, recipeId: Int): RecipePlan {
        val addDto = AddRecipeToPlanDto(recipeId = recipeId)
        return api.addRecipeToPlan(planId, addDto).toPlanDomain()
    }

    override suspend fun getPlanRecipes(planId: Int): List<Recipe> {
        return try {
            api.getPlanRecipes(planId).map { it.toRecipeDomainFromDto() }
        } catch (e: Exception) {
            recipePlanDao.getRecipesForPlan(planId).first().map { 
                it.toRecipeDomainFromEntity().copy(isDownloaded = true) 
            }
        }
    }

    override suspend fun deleteRecipePlan(planId: Int) {
        try { api.deleteRecipePlan(planId) } catch (_: Exception) { }
        recipePlanDao.deleteRecipePlan(planId)
        recipePlanDao.deleteRecipePlanCrossRefs(planId)
    }

    override suspend fun removeRecipeFromPlan(planId: Int, recipeId: Int): RecipePlan {
        return api.removeRecipeFromPlan(planId, recipeId).toPlanDomain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getDownloadedRecipePlans(): Flow<List<RecipePlan>> {
        return recipePlanDao.getDownloadedRecipePlans().flatMapLatest { planEntities ->
            if (planEntities.isEmpty()) {
                flowOf(emptyList())
            } else {
                val plansFlows = planEntities.map { entity ->
                    recipePlanDao.getRecipesForPlan(entity.id).map { recipes ->
                        entity.toPlanDomain().copy(
                            isDownloaded = true,
                            recipes = recipes.map { it.toRecipeDomainFromEntity().copy(isDownloaded = true) }
                        )
                    }
                }
                combine(plansFlows) { it.toList() }
            }
        }
    }

    override suspend fun getDownloadedPlanIds(): List<Int> = recipePlanDao.getDownloadedPlanIds()

    override suspend fun removeLocalRecipePlanDownload(planId: Int) {
        recipePlanDao.deleteRecipePlan(planId)
        recipePlanDao.deleteRecipePlanCrossRefs(planId)
    }
}
