package com.alilopez.kt_demohilt.features.user.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.features.user.domain.usecases.CreateSubscriptionUseCase
import com.alilopez.kt_demohilt.features.user.domain.usecases.GetUserUseCase
import com.alilopez.kt_demohilt.features.user.domain.usecases.PollPaymentStatusUseCase
import com.alilopez.kt_demohilt.features.user.domain.usecases.SubscriptionPollResult
import com.alilopez.kt_demohilt.features.user.presentation.screens.PremiumUIState
import com.alilopez.kt_demohilt.features.user.presentation.screens.SubscriptionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val createSubscriptionUseCase: CreateSubscriptionUseCase,
    private val pollPaymentStatusUseCase: PollPaymentStatusUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PremiumUIState())
    val uiState: StateFlow<PremiumUIState> = _uiState.asStateFlow()

    fun startCheckout() {
        val userId = sessionManager.currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, subscriptionResult = null) }
            try {
                val checkout = createSubscriptionUseCase(userId)
                _uiState.update { it.copy(isLoading = false, checkoutUrl = checkout.checkoutUrl) }
                pollSubscription(checkout.subscriptionId)
            } catch (e: HttpException) {
                val body = e.response()?.errorBody()?.string() ?: ""
                Log.e("PremiumViewModel", "HTTP ${e.code()} al crear suscripción: $body")
                val message = when (e.code()) {
                    400 -> "Ya tienes una suscripción activa o pendiente. Si ya pagaste, espera unos minutos."
                    404 -> "Usuario o plan no encontrado. Contacta soporte."
                    503 -> "El sistema de pagos no está disponible. Intenta de nuevo."
                    else -> "Error del servidor (${e.code()}). Intenta de nuevo."
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
            } catch (e: IOException) {
                Log.e("PremiumViewModel", "Error de red al crear suscripción", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Sin conexión. Verifica tu internet e intenta de nuevo."
                    )
                }
            } catch (e: Exception) {
                Log.e("PremiumViewModel", "Error inesperado al crear suscripción", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No pudimos conectar con el sistema de pagos. Intenta de nuevo."
                    )
                }
                Log.e("PremiumViewModel", "Error creating subscription", e)
            }
        }
    }

    fun onCheckoutUrlConsumed() {
        _uiState.update { it.copy(checkoutUrl = null) }
    }

    private fun pollSubscription(subscriptionId: Int) {
        val userId = sessionManager.currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isPolling = true) }
            pollPaymentStatusUseCase(subscriptionId) { result ->
                when (result) {
                    SubscriptionPollResult.Authorized -> {
                        viewModelScope.launch {
                            try {
                                val user = getUserUseCase(userId)
                                sessionManager.saveSession(
                                    userId = userId,
                                    token = sessionManager.accessToken ?: "",
                                    membership = user.membership
                                )
                            } catch (e: Exception) {
                                // Refresh is best-effort
                            }
                            _uiState.update {
                                it.copy(
                                    isPolling = false,
                                    subscriptionResult = SubscriptionResult.AUTHORIZED,
                                    isSuccess = true
                                )
                            }
                        }
                    }
                    SubscriptionPollResult.Paused ->
                        _uiState.update { it.copy(isPolling = false, subscriptionResult = SubscriptionResult.PAUSED) }
                    SubscriptionPollResult.Cancelled ->
                        _uiState.update { it.copy(isPolling = false, subscriptionResult = SubscriptionResult.CANCELLED) }
                    SubscriptionPollResult.Timeout ->
                        _uiState.update { it.copy(isPolling = false, subscriptionResult = SubscriptionResult.TIMEOUT) }
                }
            }
        }
    }
}
