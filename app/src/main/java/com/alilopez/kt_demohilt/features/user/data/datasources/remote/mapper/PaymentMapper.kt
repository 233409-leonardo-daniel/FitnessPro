package com.alilopez.kt_demohilt.features.user.data.datasources.remote.mapper

import com.alilopez.kt_demohilt.BuildConfig
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.CreateSubscriptionResponseDto
import com.alilopez.kt_demohilt.features.user.domain.entities.SubscriptionCheckout
import com.alilopez.kt_demohilt.features.user.domain.entities.SubscriptionStatus

fun CreateSubscriptionResponseDto.toDomain(): SubscriptionCheckout {
    val url = if (BuildConfig.FLAVOR == "prod") initPoint else (sandboxInitPoint ?: initPoint)
    return SubscriptionCheckout(
        subscriptionId = subscriptionId,
        checkoutUrl = url
    )
}

fun String.toSubscriptionStatus(): SubscriptionStatus {
    return when (this.lowercase()) {
        "authorized" -> SubscriptionStatus.AUTHORIZED
        "paused"     -> SubscriptionStatus.PAUSED
        "cancelled"  -> SubscriptionStatus.CANCELLED
        "pending"    -> SubscriptionStatus.PENDING
        else         -> SubscriptionStatus.UNKNOWN
    }
}
