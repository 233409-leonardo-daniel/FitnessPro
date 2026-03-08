package com.alilopez.kt_demohilt.features.user.presentation.screens

data class RegisterUIState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistered: Boolean = false
)
