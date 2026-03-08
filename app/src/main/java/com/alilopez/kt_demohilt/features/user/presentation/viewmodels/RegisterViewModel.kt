package com.alilopez.kt_demohilt.features.user.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.features.user.domain.usecases.UserRegisterUseCase
import com.alilopez.kt_demohilt.features.user.presentation.screens.RegisterUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRegisterUseCase: UserRegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUIState())
    val uiState: StateFlow<RegisterUIState> = _uiState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _lastname = MutableStateFlow("")
    val lastname: StateFlow<String> = _lastname.asStateFlow()

    private val _birthdate = MutableStateFlow("")
    val birthdate: StateFlow<String> = _birthdate.asStateFlow()

    private val _weight = MutableStateFlow("")
    val weight: StateFlow<String> = _weight.asStateFlow()

    private val _height = MutableStateFlow("")
    val height: StateFlow<String> = _height.asStateFlow()

    private val _gender = MutableStateFlow("")
    val gender: StateFlow<String> = _gender.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onNameChange(name: String) {
        _name.value = name
    }

    fun onLastnameChange(lastname: String) {
        _lastname.value = lastname
    }

    fun onBirthdateChange(birthdate: String) {
        _birthdate.value = birthdate
    }

    fun onWeightChange(weight: String) {
        _weight.value = weight
    }

    fun onHeightChange(height: String) {
        _height.value = height
    }

    fun onGenderChange(gender: String) {
        _gender.value = gender
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onRegisterClick() {
        viewModelScope.launch {
            val weightValue = _weight.value.toDoubleOrNull()
            val heightValue = _height.value.toDoubleOrNull()

            if (
                _email.value.isBlank() ||
                _name.value.isBlank() ||
                _lastname.value.isBlank() ||
                _birthdate.value.isBlank() ||
                _gender.value.isBlank() ||
                _password.value.isBlank() ||
                weightValue == null ||
                heightValue == null
            ) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Completa todos los campos con valores validos"
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                userRegisterUseCase(
                    email = _email.value.trim(),
                    name = _name.value.trim(),
                    lastname = _lastname.value.trim(),
                    birthdate = _birthdate.value.trim(),
                    weight = weightValue,
                    height = heightValue,
                    gender = _gender.value.trim(),
                    password = _password.value
                )

                _uiState.update { it.copy(isRegistered = true, isLoading = false) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al registrar usuario"
                    )
                }
            }
        }
    }
}
