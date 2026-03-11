package com.alilopez.kt_demohilt.features.recipeplans.domain.usecases

import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe
import com.alilopez.kt_demohilt.features.recipeplans.domain.repositories.RecipePlanRepository
import javax.inject.Inject

class GetPlanRecipesUseCase @Inject constructor(
    private val repository: RecipePlanRepository
) {
    suspend operator fun invoke(planId: Int): Result<List<Recipe>> {
        return try {
            Result.success(repository.getPlanRecipes(planId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
