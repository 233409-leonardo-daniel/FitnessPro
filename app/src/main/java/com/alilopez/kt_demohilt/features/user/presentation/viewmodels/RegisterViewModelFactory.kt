package com.alilopez.kt_demohilt.features.user.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alilopez.kt_demohilt.features.user.domain.usecases.UserRegisterUseCase

class RegisterViewModelFactory(
    private val userRegisterUseCase: UserRegisterUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(userRegisterUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
