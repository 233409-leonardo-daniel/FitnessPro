package com.alilopez.kt_demohilt.features.user.domain.usecases

import com.alilopez.kt_demohilt.features.user.domain.entities.SubscriptionCheckout
import com.alilopez.kt_demohilt.features.user.domain.repositories.SubscriptionRepository
import javax.inject.Inject

class CreateSubscriptionUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) {
    suspend operator fun invoke(userId: Int, planId: Int = PREMIUM_PLAN_ID): SubscriptionCheckout =
        subscriptionRepository.createSubscription(userId, planId)

    companion object {
        const val PREMIUM_PLAN_ID = 3
    }
}
