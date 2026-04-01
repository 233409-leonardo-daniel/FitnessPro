package com.alilopez.kt_demohilt.features.user.domain.repositories

interface PaymentRepository {
    // Devuelve Pair(preferenceId, checkoutUrl) — la URL ya está seleccionada según el flavor
    suspend fun createCheckout(userId: Int): Pair<String, String>
    suspend fun getPaymentStatus(preferenceId: String): String
}
