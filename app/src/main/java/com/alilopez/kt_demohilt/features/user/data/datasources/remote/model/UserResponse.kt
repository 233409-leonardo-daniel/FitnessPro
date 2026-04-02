package com.alilopez.kt_demohilt.features.user.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class UsersResponse(
    val data: List<UserDto>
)

data class UserDto(
    val id: Int,
    val email: String,
    val name: String,
    val lastname: String,
    val birthdate: String?,
    val weight: Double?,
    val height: Double?,
    val gender: String?,
    val age: Int? = null,
    val membership: String? = null,
    @SerializedName("target_weight") val targetWeight: Float? = null
)

data class UserCreateDto(
    val email: String,
    val name: String,
    val lastname: String,
    val birthdate: String? = "",
    val weight: Double? = 0.0,
    val height: Double? = 0.0,
    val gender: String? = "Otro",
    val password: String
)

data class UserUpdateDto(
    val email: String,
    val name: String,
    val lastname: String,
    val birthdate: String?,
    val weight: Double?,
    val height: Double?,
    val gender: String?,
    val membership: String? = "gratuito",
    val password: String? = null,
    @SerializedName("target_weight") val targetWeight: Float? = null
)

data class UserLoginResponseDto(
    val id: Int,
    val email: String,
    val name: String,
    val lastname: String,
    val birthdate: String? = null,
    val weight: Double? = null,
    val height: Double? = null,
    val gender: String? = null,
    val age: Int? = null,
    val access_token: String,
    val token_type: String,
    val membership: String? = null,
    @SerializedName("target_weight") val targetWeight: Float? = null
)

data class GoogleErrorResponseDto(
    val code: String,
    val email: String? = null,
    val name: String? = null,
    @SerializedName("lastname") val lastName: String? = null
)
