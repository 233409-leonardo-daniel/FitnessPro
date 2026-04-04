package com.alilopez.kt_demohilt.features.progression.data.datasources.remote.mapper

import com.alilopez.kt_demohilt.features.progression.data.datasources.remote.model.ProgressionEntryDto
import com.alilopez.kt_demohilt.features.progression.data.datasources.remote.model.ProgressionSummaryDto
import com.alilopez.kt_demohilt.features.progression.domain.entities.ProgressionEntry
import com.alilopez.kt_demohilt.features.progression.domain.entities.ProgressionSummary

fun ProgressionEntryDto.toDomain(): ProgressionEntry {
    return ProgressionEntry(
        id = id,
        userId = userId,
        weight = weight,
        date = date
    )
}

fun ProgressionSummaryDto.toDomain(): ProgressionSummary {
    return ProgressionSummary(
        userId = userId,
        targetWeight = targetWeight,
        initialWeight = initialWeight,
        currentWeight = currentWeight,
        weightChange = weightChange,
        entriesCount = entriesCount,
        onTrack = onTrack,
        trend = trend
    )
}
