package com.alilopez.kt_demohilt.features.user.domain.repositories

import com.alilopez.kt_demohilt.features.user.domain.entities.SubscriptionCheckout
import com.alilopez.kt_demohilt.features.user.domain.entities.SubscriptionStatus

interface SubscriptionRepository {
    suspend fun createSubscription(userId: Int, planId: Int): SubscriptionCheckout
    suspend fun getSubscriptionStatus(subscriptionId: Int): SubscriptionStatus
}
