package com.alilopez.kt_demohilt.features.user.data.datasources.remote.model

data class UsersResponse(
    val data: List<UserDto>
)

data class UserDto(
    val id: Int,
    val email: String,
    val name: String,
    val lastname: String
)

data class UserCreateDto(
    val email: String,
    val name: String,
    val lastname: String,
    val password: String
)

data class UserLoginResponseDto(
    val access_token: String,
    val token_type: String,
    val user: UserDto?
)
