package com.alilopez.kt_demohilt.features.user.domain.usecases

import com.alilopez.kt_demohilt.features.user.domain.entities.User
import com.alilopez.kt_demohilt.features.user.domain.repositories.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: Int): User {
        return userRepository.getUser(id)
    }
}
