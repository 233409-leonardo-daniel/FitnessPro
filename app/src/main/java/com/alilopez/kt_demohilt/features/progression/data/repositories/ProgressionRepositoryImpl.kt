package com.alilopez.kt_demohilt.features.progression.data.repositories

import com.alilopez.kt_demohilt.core.network.FitnessProApi
import com.alilopez.kt_demohilt.features.progression.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.progression.data.datasources.remote.model.AddProgressionEntryDto
import com.alilopez.kt_demohilt.features.progression.domain.entities.ProgressionEntry
import com.alilopez.kt_demohilt.features.progression.domain.entities.ProgressionSummary
import com.alilopez.kt_demohilt.features.progression.domain.repositories.ProgressionRepository
import javax.inject.Inject

class ProgressionRepositoryImpl @Inject constructor(
    private val api: FitnessProApi
) : ProgressionRepository {
    override suspend fun getProgressionHistory(userId: Int): List<ProgressionEntry> {
        return api.getProgressionHistory(userId).map { it.toDomain() }
    }

    override suspend fun getProgressionSummary(userId: Int): ProgressionSummary {
        return api.getProgressionSummary(userId).toDomain()
    }

    override suspend fun addProgressionEntry(userId: Int, weight: Float): ProgressionEntry {
        return api.addProgressionEntry(userId, AddProgressionEntryDto(weight)).toDomain()
    }
}
