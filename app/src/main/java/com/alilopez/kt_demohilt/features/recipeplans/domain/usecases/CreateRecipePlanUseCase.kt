package com.alilopez.kt_demohilt.features.recipeplans.domain.usecases

import com.alilopez.kt_demohilt.features.recipeplans.domain.entities.RecipePlan
import com.alilopez.kt_demohilt.features.recipeplans.domain.repositories.RecipePlanRepository
import javax.inject.Inject

class CreateRecipePlanUseCase @Inject constructor(
    private val repository: RecipePlanRepository
) {
    suspend operator fun invoke(name: String, description: String, userId: Int, isPrivate: Boolean): Result<RecipePlan> {
        return try {
            Result.success(repository.createRecipePlan(name, description, userId, isPrivate))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
