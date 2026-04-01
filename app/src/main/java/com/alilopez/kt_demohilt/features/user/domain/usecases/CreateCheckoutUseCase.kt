package com.alilopez.kt_demohilt.features.user.domain.usecases

import com.alilopez.kt_demohilt.features.user.domain.repositories.PaymentRepository
import javax.inject.Inject

class CreateCheckoutUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(userId: Int): Pair<String, String> =
        paymentRepository.createCheckout(userId)
}
