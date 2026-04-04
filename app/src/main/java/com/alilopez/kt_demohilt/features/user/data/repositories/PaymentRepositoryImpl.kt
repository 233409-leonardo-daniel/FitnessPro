package com.alilopez.kt_demohilt.features.user.data.repositories

import com.alilopez.kt_demohilt.core.network.FitnessProApi
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.mapper.toSubscriptionStatus
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.CreateSubscriptionRequestDto
import com.alilopez.kt_demohilt.features.user.domain.entities.SubscriptionCheckout
import com.alilopez.kt_demohilt.features.user.domain.entities.SubscriptionException
import com.alilopez.kt_demohilt.features.user.domain.entities.SubscriptionStatus
import com.alilopez.kt_demohilt.features.user.domain.repositories.SubscriptionRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val api: FitnessProApi
) : SubscriptionRepository {

    override suspend fun createSubscription(userId: Int, planId: Int): SubscriptionCheckout {
        try {
            return api.createSubscription(CreateSubscriptionRequestDto(userId, planId)).toDomain()
        } catch (e: HttpException) {
            throw when (e.code()) {
                400 -> SubscriptionException.ActiveSubscriptionExists()
                404 -> SubscriptionException.UserOrPlanNotFound()
                503 -> SubscriptionException.ServiceUnavailable()
                else -> SubscriptionException.Unexpected(e)
            }
        } catch (e: IOException) {
            throw SubscriptionException.NetworkUnavailable()
        }
    }

    override suspend fun getSubscriptionStatus(subscriptionId: Int): SubscriptionStatus {
        return api.getSubscriptionStatus(subscriptionId).status.toSubscriptionStatus()
    }
}
