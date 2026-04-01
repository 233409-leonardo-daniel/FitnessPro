package com.alilopez.kt_demohilt.features.user.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class PaymentCheckoutRequestDto(
    @SerializedName("user_id") val userId: Int
)

data class PaymentCheckoutResponseDto(
    @SerializedName("preference_id") val preferenceId: String,
    @SerializedName("init_point") val initPoint: String,
    @SerializedName("sandbox_init_point") val sandboxInitPoint: String
)

data class PaymentStatusResponseDto(
    @SerializedName("preference_id") val preferenceId: String,
    val status: String,   // "pending" | "approved" | "rejected"
    @SerializedName("user_id") val userId: Int,
    val amount: Double,
    val currency: String
)
