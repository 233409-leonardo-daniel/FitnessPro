package com.alilopez.kt_demohilt.features.recipeplans.domain.usecases

import com.alilopez.kt_demohilt.features.recipeplans.domain.repositories.RecipePlanRepository
import javax.inject.Inject

class RemoveLocalRecipePlanDownloadUseCase @Inject constructor(
    private val repository: RecipePlanRepository
) {
    suspend operator fun invoke(planId: Int): Result<Unit> {
        return try {
            repository.removeLocalRecipePlanDownload(planId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
