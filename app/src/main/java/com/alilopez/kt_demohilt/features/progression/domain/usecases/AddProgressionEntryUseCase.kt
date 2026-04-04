package com.alilopez.kt_demohilt.features.progression.domain.usecases

import com.alilopez.kt_demohilt.features.progression.domain.entities.ProgressionEntry
import com.alilopez.kt_demohilt.features.progression.domain.repositories.ProgressionRepository
import javax.inject.Inject

class AddProgressionEntryUseCase @Inject constructor(
    private val repository: ProgressionRepository
) {
    suspend operator fun invoke(userId: Int, weight: Float): ProgressionEntry {
        return repository.addProgressionEntry(userId, weight)
    }
}
