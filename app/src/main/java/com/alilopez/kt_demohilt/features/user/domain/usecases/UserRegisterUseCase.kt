package com.alilopez.kt_demohilt.features.user.domain.usecases

import com.alilopez.kt_demohilt.features.user.domain.entities.User
import com.alilopez.kt_demohilt.features.user.domain.repositories.UserRepository
import javax.inject.Inject

class UserRegisterUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        email: String,
        name: String,
        lastname: String,
        birthdate: String? = null,
        weight: Double? = null,
        height: Double? = null,
        gender: String? = null,
        password: String
    ): User {
        return userRepository.register(
            email = email,
            name = name,
            lastname = lastname,
            birthdate = birthdate,
            weight = weight,
            height = height,
            gender = gender,
            password = password
        )
    }
}
