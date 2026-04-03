package com.alilopez.kt_demohilt.features.user.domain.usecases

import com.alilopez.kt_demohilt.features.user.domain.entities.PaymentStatus
import com.alilopez.kt_demohilt.features.user.domain.repositories.PaymentRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

sealed class PaymentPollResult {
    object Approved : PaymentPollResult()
    object Rejected : PaymentPollResult()
    object Timeout  : PaymentPollResult()
}

class PollPaymentStatusUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(
        preferenceId: String,
        maxAttempts: Int = 30,
        intervalMs: Long = 3000L,
        onResult: (PaymentPollResult) -> Unit
    ) {
        repeat(maxAttempts) {
            delay(intervalMs)
            try {
                when (paymentRepository.getPaymentStatus(preferenceId)) {
                    PaymentStatus.APPROVED -> {
                        onResult(PaymentPollResult.Approved)
                        return
                    }
                    PaymentStatus.REJECTED -> {
                        onResult(PaymentPollResult.Rejected)
                        return
                    }
                    PaymentStatus.PENDING -> {
                        // Continue polling
                    }
                    PaymentStatus.UNKNOWN -> {
                        // Could be an error or unexpected status, continue polling for now
                    }
                }
            } catch (e: Exception) {
                // Network error - continue to next attempt (retry logic)
            }
        }
        onResult(PaymentPollResult.Timeout)
    }
}
