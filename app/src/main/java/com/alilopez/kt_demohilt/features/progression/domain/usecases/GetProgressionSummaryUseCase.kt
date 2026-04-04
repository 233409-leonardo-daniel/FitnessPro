package com.alilopez.kt_demohilt.features.progression.domain.usecases

import com.alilopez.kt_demohilt.features.progression.domain.entities.ProgressionSummary
import com.alilopez.kt_demohilt.features.progression.domain.repositories.ProgressionRepository
import javax.inject.Inject

class GetProgressionSummaryUseCase @Inject constructor(
    private val repository: ProgressionRepository
) {
    suspend operator fun invoke(userId: Int): ProgressionSummary {
        return repository.getProgressionSummary(userId)
    }
}
