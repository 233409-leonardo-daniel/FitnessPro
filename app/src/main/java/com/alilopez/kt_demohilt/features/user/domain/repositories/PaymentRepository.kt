package com.alilopez.kt_demohilt.features.user.domain.repositories

import com.alilopez.kt_demohilt.features.user.domain.entities.PaymentCheckout
import com.alilopez.kt_demohilt.features.user.domain.entities.PaymentStatus

interface PaymentRepository {
    suspend fun createCheckout(userId: Int): PaymentCheckout
    suspend fun getPaymentStatus(preferenceId: String): PaymentStatus
}
