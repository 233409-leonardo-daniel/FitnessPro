package com.alilopez.kt_demohilt.features.user.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.GoogleErrorResponseDto
import com.alilopez.kt_demohilt.features.user.domain.usecases.LoginWithGoogleUseCase
import com.alilopez.kt_demohilt.features.user.domain.usecases.UserLoginUseCase
import com.alilopez.kt_demohilt.features.user.domain.usecases.UserRegisterUseCase
import com.alilopez.kt_demohilt.features.user.presentation.screens.LoginUIState
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userLoginUseCase: UserLoginUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val userRegisterUseCase: UserRegisterUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = userLoginUseCase(
                    email = _email.value,
                    password = _password.value
                )
                handleLoginSuccess(response.id, response.access_token, response.membership)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al iniciar sesión"
                    )
                }
            }
        }
    }

    fun onGoogleLoginSuccess(idToken: String) {
        Log.d("GoogleLogin", "Token recibido de Google: ${idToken.take(20)}...")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            loginWithGoogleUseCase(idToken).onSuccess { response ->
                Log.d("GoogleLogin", "Respuesta del Servidor: ID=${response.id}, Token=${response.access_token.take(10)}...")
                handleLoginSuccess(response.id, response.access_token, response.membership)
            }.onFailure { e ->
                if (e is HttpException && e.code() == 404) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val googleError = try {
                        Gson().fromJson(errorBody, GoogleErrorResponseDto::class.java)
                    } catch (ex: Exception) {
                        null
                    }

                    if (googleError?.code == "GOOGLE_USER_NOT_REGISTERED") {
                        handleAutoRegister(googleError.email ?: "", googleError.name ?: "", googleError.lastName ?: "", idToken)
                    } else {
                        handleGoogleError(e)
                    }
                } else {
                    handleGoogleError(e)
                }
            }
        }
    }

    private fun handleAutoRegister(email: String, name: String, lastName: String, idToken: String) {
        viewModelScope.launch {
            try {
                Log.d("GoogleLogin", "Iniciando registro automático para: $email")
                userRegisterUseCase(
                    email = email,
                    name = name,
                    lastname = lastName,
                    password = "GoogleLoginPass123!" // Contraseña genérica
                )
                // Después de registrar, reintentamos el login con Google
                onGoogleLoginSuccess(idToken)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error en el registro automático: ${e.message}"
                    )
                }
            }
        }
    }

    private fun handleGoogleError(e: Throwable) {
        Log.e("GoogleLogin", "Error en la petición al Servidor: ${e.message}")
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = "Error del servidor: ${e.message}"
            )
        }
    }

    fun onGoogleLoginError(message: String) {
        Log.e("GoogleLogin", "Error de CredentialManager: $message")
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = message
            )
        }
    }

    private fun handleLoginSuccess(userId: Int, token: String, membership: String? = null) {
        if (token.isNotEmpty()) {
            Log.d("GoogleLogin", "Login exitoso. Guardando sesión...")
            sessionManager.saveSession(userId, token, membership)
            _uiState.update { it.copy(isLoggedIn = true, isLoading = false) }
        } else {
            Log.w("GoogleLogin", "Login fallido: El token del servidor está vacío")
            _uiState.update {
                it.copy(
                    errorMessage = "El servidor no devolvió un token válido",
                    isLoading = false
                )
            }
        }
    }
}
