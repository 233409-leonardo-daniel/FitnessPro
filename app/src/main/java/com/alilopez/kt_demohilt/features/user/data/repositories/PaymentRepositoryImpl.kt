package com.alilopez.kt_demohilt.features.user.data.repositories

import com.alilopez.kt_demohilt.core.network.FitnessProApi
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.mapper.toSubscriptionStatus
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.CreateSubscriptionRequestDto
import com.alilopez.kt_demohilt.features.user.domain.entities.SubscriptionCheckout
import com.alilopez.kt_demohilt.features.user.domain.entities.SubscriptionStatus
import com.alilopez.kt_demohilt.features.user.domain.repositories.SubscriptionRepository
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val api: FitnessProApi
) : SubscriptionRepository {

    override suspend fun createSubscription(userId: Int, planId: Int): SubscriptionCheckout {
        return api.createSubscription(CreateSubscriptionRequestDto(userId, planId)).toDomain()
    }

    override suspend fun getSubscriptionStatus(subscriptionId: Int): SubscriptionStatus {
        return api.getSubscriptionStatus(subscriptionId).status.toSubscriptionStatus()
    }
}
