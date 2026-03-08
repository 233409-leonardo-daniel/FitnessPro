package com.alilopez.kt_demohilt.features.user.data.repositories

import com.alilopez.kt_demohilt.core.network.FitnessProApi
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.UserCreateDto
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.UserLoginResponseDto
import com.alilopez.kt_demohilt.features.user.domain.entities.User
import com.alilopez.kt_demohilt.features.user.domain.repositories.UserRepository
import javax.inject.Inject

class UserRepositoryImp @Inject constructor(
    private val fitnessProApi: FitnessProApi
): UserRepository {

    override suspend fun register(
        email: String,
        name: String,
        lastname: String,
        password: String
    ): User {
        val userCreateDto = UserCreateDto(
            email = email,
            name = name,
            lastname = lastname,
            password = password
        )

        val userDto = fitnessProApi.register(userCreateDto)
        return userDto.toDomain()
    }

    override suspend fun login(
        email: String,
        password: String
    ): UserLoginResponseDto {
        return fitnessProApi.login(
            email = email,
            password = password
        )
    }

    override suspend fun getUser(id: Int): User {
        val userDto = fitnessProApi.getUser(id)
        return userDto.toDomain()
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return false
    }
}
