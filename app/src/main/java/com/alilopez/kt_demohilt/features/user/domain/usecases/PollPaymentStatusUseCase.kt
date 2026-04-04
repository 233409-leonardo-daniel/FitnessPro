package com.alilopez.kt_demohilt.features.user.domain.usecases

import com.alilopez.kt_demohilt.features.user.domain.entities.SubscriptionStatus
import com.alilopez.kt_demohilt.features.user.domain.repositories.SubscriptionRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

sealed class SubscriptionPollResult {
    object Authorized : SubscriptionPollResult()
    object Paused     : SubscriptionPollResult()
    object Cancelled  : SubscriptionPollResult()
    object Timeout    : SubscriptionPollResult()
}

class PollPaymentStatusUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) {
    suspend operator fun invoke(
        subscriptionId: Int,
        maxAttempts: Int = 30,
        intervalMs: Long = 3000L,
        onResult: (SubscriptionPollResult) -> Unit
    ) {
        repeat(maxAttempts) {
            delay(intervalMs)
            try {
                when (subscriptionRepository.getSubscriptionStatus(subscriptionId)) {
                    SubscriptionStatus.AUTHORIZED -> { onResult(SubscriptionPollResult.Authorized); return }
                    SubscriptionStatus.PAUSED     -> { onResult(SubscriptionPollResult.Paused);     return }
                    SubscriptionStatus.CANCELLED  -> { onResult(SubscriptionPollResult.Cancelled);  return }
                    SubscriptionStatus.PENDING, SubscriptionStatus.UNKNOWN -> Unit
                }
            } catch (_: Exception) { }
        }
        onResult(SubscriptionPollResult.Timeout)
    }
}
