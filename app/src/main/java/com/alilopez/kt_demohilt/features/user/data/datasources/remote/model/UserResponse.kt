package com.alilopez.kt_demohilt.features.user.data.datasources.remote.model

data class UsersResponse(
    val data: List<UserDto>
)

data class UserDto(
    val id: Int,
    val email: String,
    val name: String,
    val lastname: String,
    val birthdate: String,
    val weight: Double,
    val height: Double,
    val gender: String,
    val age: Int? = null,
    val membership: String? = null
)

data class UserCreateDto(
    val email: String,
    val name: String,
    val lastname: String,
    val birthdate: String,
    val weight: Double,
    val height: Double,
    val gender: String,
    val password: String
)

data class UserLoginResponseDto(
    val id: Int,
    val email: String,
    val name: String,
    val lastname: String,
    val birthdate: String,
    val weight: Double,
    val height: Double,
    val gender: String,
    val age: Int,
    val access_token: String,
    val token_type: String,
    val membership: String? = null
)
