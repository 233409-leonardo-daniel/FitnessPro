package com.alilopez.kt_demohilt.features.recipeplans.domain.usecases

import com.alilopez.kt_demohilt.features.recipeplans.domain.entities.RecipePlan
import com.alilopez.kt_demohilt.features.recipeplans.domain.repositories.RecipePlanRepository
import javax.inject.Inject

class GetUserRecipePlansUseCase @Inject constructor(
    private val repository: RecipePlanRepository
) {
    suspend operator fun invoke(userId: Int): Result<List<RecipePlan>> {
        return try {
            Result.success(repository.getUserRecipePlans(userId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
