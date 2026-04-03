package com.alilopez.kt_demohilt.features.user.data.repositories

import com.alilopez.kt_demohilt.core.network.FitnessProApi
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.mapper.toPaymentStatus
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.PaymentCheckoutRequestDto
import com.alilopez.kt_demohilt.features.user.domain.entities.PaymentCheckout
import com.alilopez.kt_demohilt.features.user.domain.entities.PaymentStatus
import com.alilopez.kt_demohilt.features.user.domain.repositories.PaymentRepository
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val api: FitnessProApi
) : PaymentRepository {

    override suspend fun createCheckout(userId: Int): PaymentCheckout {
        return api.createCheckout(PaymentCheckoutRequestDto(userId)).toDomain()
    }

    override suspend fun getPaymentStatus(preferenceId: String): PaymentStatus {
        return api.getPaymentStatus(preferenceId).status.toPaymentStatus()
    }
}
