package com.alilopez.kt_demohilt.features.progression.presentation.screens

import com.alilopez.kt_demohilt.features.progression.domain.entities.ProgressionEntry
import com.alilopez.kt_demohilt.features.progression.domain.entities.ProgressionSummary

data class ProgressionUIState(
    val isLoading: Boolean = false,
    val summary: ProgressionSummary? = null,
    val history: List<ProgressionEntry> = emptyList(),
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
