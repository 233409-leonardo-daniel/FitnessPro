package com.alilopez.kt_demohilt.features.user.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.features.user.domain.usecases.GetUserUseCase
import com.alilopez.kt_demohilt.features.user.domain.usecases.UpdateUserUseCase
import com.alilopez.kt_demohilt.features.user.presentation.screens.PremiumUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PremiumUIState())
    val uiState: StateFlow<PremiumUIState> = _uiState.asStateFlow()

    fun upgradeToPremium() {
        val userId = sessionManager.currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Primero obtenemos los datos actuales para no sobreescribir con nulos
                val currentUser = getUserUseCase(userId)
                
                // Actualizamos a premium
                updateUserUseCase(
                    id = userId,
                    email = currentUser.email,
                    name = currentUser.name,
                    lastname = currentUser.lastname,
                    birthdate = currentUser.birthdate,
                    weight = currentUser.weight,
                    height = currentUser.height,
                    gender = currentUser.gender,
                    membership = "premium"
                )
                
                // Actualizamos la sesión local
                sessionManager.saveSession(
                    userId = userId,
                    token = sessionManager.accessToken ?: "",
                    membership = "premium"
                )
                
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
