package com.alilopez.kt_demohilt.features.user.domain.repositories

import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.UserLoginResponseDto
import com.alilopez.kt_demohilt.features.user.domain.entities.User

interface UserRepository {
    suspend fun isUserLoggedIn(): Boolean
    suspend fun getUser(id: Int): User
    suspend fun login(email: String, password: String): UserLoginResponseDto
    suspend fun register(email: String, name: String, lastname: String, password: String): User
}
