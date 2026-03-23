package com.alilopez.kt_demohilt.features.user.domain.usecases

import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.UserLoginResponseDto
import com.alilopez.kt_demohilt.features.user.domain.repositories.UserRepository
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(idToken: String): Result<UserLoginResponseDto> {
        return try {
            val response = userRepository.loginWithGoogle(idToken)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
