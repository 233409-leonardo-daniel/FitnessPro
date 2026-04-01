package com.alilopez.kt_demohilt.features.user.data.repositories

import com.alilopez.kt_demohilt.BuildConfig
import com.alilopez.kt_demohilt.core.network.FitnessProApi
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.PaymentCheckoutRequestDto
import com.alilopez.kt_demohilt.features.user.domain.repositories.PaymentRepository
import javax.inject.Inject

class PaymentRepositoryImp @Inject constructor(
    private val api: FitnessProApi
) : PaymentRepository {

    override suspend fun createCheckout(userId: Int): Pair<String, String> {
        val response = api.createCheckout(PaymentCheckoutRequestDto(userId))
        val url = if (BuildConfig.FLAVOR == "prod") response.initPoint
                  else response.sandboxInitPoint
        return Pair(response.preferenceId, url)
    }

    override suspend fun getPaymentStatus(preferenceId: String): String {
        return api.getPaymentStatus(preferenceId).status
    }
}
