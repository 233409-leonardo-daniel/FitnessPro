package com.alilopez.kt_demohilt.features.user.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class CreateSubscriptionRequestDto(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("plan_id") val planId: Int,
    @SerializedName("card_token_id") val cardTokenId: String? = null
)

data class CreateSubscriptionResponseDto(
    @SerializedName("subscription_id") val subscriptionId: Int,
    @SerializedName("mp_preapproval_id") val mpPreapprovalId: String,
    @SerializedName("init_point") val initPoint: String,
    @SerializedName("sandbox_init_point") val sandboxInitPoint: String?,
    val status: String,
    @SerializedName("plan_name") val planName: String,
    val amount: Double,
    val currency: String
)

data class SubscriptionStatusResponseDto(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val status: String, // "pending" | "authorized" | "paused" | "cancelled"
    @SerializedName("transaction_amount") val transactionAmount: Double,
    @SerializedName("currency_id") val currencyId: String,
    @SerializedName("next_payment_date") val nextPaymentDate: String?,
    @SerializedName("last_payment_date") val lastPaymentDate: String?,
    @SerializedName("payments_count") val paymentsCount: Int,
    @SerializedName("failed_payments_count") val failedPaymentsCount: Int,
    @SerializedName("is_active") val isActive: Boolean
)
