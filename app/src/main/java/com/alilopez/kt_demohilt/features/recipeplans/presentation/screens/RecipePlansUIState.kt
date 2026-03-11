package com.alilopez.kt_demohilt.features.recipeplans.presentation.screens

import com.alilopez.kt_demohilt.features.recipeplans.domain.entities.RecipePlan

data class RecipePlansUIState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val recipePlans: List<RecipePlan> = emptyList(),
    val planCreated: Boolean = false,
    val planDeleted: Boolean = false
)
