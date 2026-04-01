package com.alilopez.kt_demohilt.features.user.presentation.screens

enum class PaymentResult { APPROVED, REJECTED, TIMEOUT }

data class PremiumUIState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val checkoutUrl: String? = null,
    val isPolling: Boolean = false,
    val paymentResult: PaymentResult? = null
)
