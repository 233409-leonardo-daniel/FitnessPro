package com.alilopez.kt_demohilt.features.user.presentation.screens

import com.alilopez.kt_demohilt.features.user.domain.entities.User

data class ProfileUIState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val user: User? = null
)
