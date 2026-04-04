package com.alilopez.kt_demohilt.features.progression.domain.usecases

import com.alilopez.kt_demohilt.features.progression.domain.entities.ProgressionEntry
import com.alilopez.kt_demohilt.features.progression.domain.repositories.ProgressionRepository
import javax.inject.Inject

class GetProgressionHistoryUseCase @Inject constructor(
    private val repository: ProgressionRepository
) {
    suspend operator fun invoke(userId: Int): List<ProgressionEntry> {
        return repository.getProgressionHistory(userId)
    }
}
