package com.alilopez.kt_demohilt.features.user.presentation.screens

enum class SubscriptionResult { AUTHORIZED, PAUSED, CANCELLED, TIMEOUT }

data class PremiumUIState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val checkoutUrl: String? = null,
    val isPolling: Boolean = false,
    val subscriptionResult: SubscriptionResult? = null
)
