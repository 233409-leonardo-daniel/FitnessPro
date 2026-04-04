package com.alilopez.kt_demohilt.features.progression.domain.repositories

import com.alilopez.kt_demohilt.features.progression.domain.entities.ProgressionEntry
import com.alilopez.kt_demohilt.features.progression.domain.entities.ProgressionSummary

interface ProgressionRepository {
    suspend fun getProgressionHistory(userId: Int): List<ProgressionEntry>
    suspend fun getProgressionSummary(userId: Int): ProgressionSummary
    suspend fun addProgressionEntry(userId: Int, weight: Float): ProgressionEntry
}
