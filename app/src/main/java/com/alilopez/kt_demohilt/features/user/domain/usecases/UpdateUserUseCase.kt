package com.alilopez.kt_demohilt.features.user.domain.usecases

import com.alilopez.kt_demohilt.features.user.domain.entities.User
import com.alilopez.kt_demohilt.features.user.domain.repositories.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        id: Int,
        email: String,
        name: String,
        lastname: String,
        birthdate: String?,
        weight: Double?,
        height: Double?,
        gender: String?,
        membership: String?
    ): User {
        return userRepository.updateUser(
            id = id,
            email = email,
            name = name,
            lastname = lastname,
            birthdate = birthdate,
            weight = weight,
            height = height,
            gender = gender,
            membership = membership
        )
    }
}
