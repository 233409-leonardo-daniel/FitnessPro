package com.alilopez.kt_demohilt.features.user.presentation.screens

data class LoginUIState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)
