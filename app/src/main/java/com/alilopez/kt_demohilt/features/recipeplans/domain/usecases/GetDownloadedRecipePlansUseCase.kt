package com.alilopez.kt_demohilt.features.recipeplans.domain.usecases

import com.alilopez.kt_demohilt.features.recipeplans.domain.entities.RecipePlan
import com.alilopez.kt_demohilt.features.recipeplans.domain.repositories.RecipePlanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDownloadedRecipePlansUseCase @Inject constructor(
    private val repository: RecipePlanRepository
) {
    operator fun invoke(): Flow<List<RecipePlan>> = repository.getDownloadedRecipePlans()
}
