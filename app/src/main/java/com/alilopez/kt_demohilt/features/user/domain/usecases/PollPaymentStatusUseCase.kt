package com.alilopez.kt_demohilt.features.user.domain.usecases

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
                    "approved" -> { onResult(PaymentPollResult.Approved); return }
                    "rejected" -> { onResult(PaymentPollResult.Rejected); return }
                }
                // "pending" — continúa al siguiente intento
            } catch (e: Exception) {
                // error de red — continúa al siguiente intento
            }
        }
        onResult(PaymentPollResult.Timeout)
    }
}
