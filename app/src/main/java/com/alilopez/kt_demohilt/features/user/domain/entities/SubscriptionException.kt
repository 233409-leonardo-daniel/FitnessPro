package com.alilopez.kt_demohilt.features.user.domain.entities

sealed class SubscriptionException(message: String) : Exception(message) {
    class ActiveSubscriptionExists : SubscriptionException("Active subscription already exists")
    class UserOrPlanNotFound : SubscriptionException("User or plan not found")
    class ServiceUnavailable : SubscriptionException("Payment service unavailable")
    class NetworkUnavailable : SubscriptionException("Network unavailable")
    class Unexpected(cause: Throwable) : SubscriptionException(cause.message ?: "Unexpected error")
}
