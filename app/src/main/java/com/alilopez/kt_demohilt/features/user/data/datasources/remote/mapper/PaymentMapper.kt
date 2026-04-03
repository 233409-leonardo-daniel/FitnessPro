package com.alilopez.kt_demohilt.features.user.data.datasources.remote.mapper

import com.alilopez.kt_demohilt.BuildConfig
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.PaymentCheckoutResponseDto
import com.alilopez.kt_demohilt.features.user.domain.entities.PaymentCheckout
import com.alilopez.kt_demohilt.features.user.domain.entities.PaymentStatus

fun PaymentCheckoutResponseDto.toDomain(): PaymentCheckout {
    val url = if (BuildConfig.FLAVOR == "prod") initPoint else sandboxInitPoint
    return PaymentCheckout(
        preferenceId = preferenceId,
        checkoutUrl = url
    )
}

fun String.toPaymentStatus(): PaymentStatus {
    return when (this.lowercase()) {
        "approved" -> PaymentStatus.APPROVED
        "rejected" -> PaymentStatus.REJECTED
        "pending" -> PaymentStatus.PENDING
        else -> PaymentStatus.UNKNOWN
    }
}
