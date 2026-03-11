package com.alilopez.kt_demohilt.features.recipeplans.domain.usecases

import com.alilopez.kt_demohilt.features.recipeplans.domain.entities.RecipePlan
import com.alilopez.kt_demohilt.features.recipeplans.domain.repositories.RecipePlanRepository
import javax.inject.Inject

class AddRecipeToPlanUseCase @Inject constructor(
    private val repository: RecipePlanRepository
) {
    suspend operator fun invoke(planId: Int, recipeId: Int): Result<RecipePlan> {
        return try {
            Result.success(repository.addRecipeToPlan(planId, recipeId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
