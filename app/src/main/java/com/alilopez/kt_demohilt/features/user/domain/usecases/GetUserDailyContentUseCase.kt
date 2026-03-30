package com.alilopez.kt_demohilt.features.user.domain.usecases

import com.alilopez.kt_demohilt.features.user.domain.entities.UserDailyContent
import com.alilopez.kt_demohilt.features.user.domain.repositories.UserRepository
import javax.inject.Inject

class GetUserDailyContentUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Int): UserDailyContent {
        return userRepository.getUserDailyContent(userId)
    }
}

