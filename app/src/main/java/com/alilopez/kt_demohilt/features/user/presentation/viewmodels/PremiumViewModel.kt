package com.alilopez.kt_demohilt.features.user.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.features.user.domain.usecases.CreateCheckoutUseCase
import com.alilopez.kt_demohilt.features.user.domain.usecases.GetUserUseCase
import com.alilopez.kt_demohilt.features.user.domain.usecases.PaymentPollResult
import com.alilopez.kt_demohilt.features.user.domain.usecases.PollPaymentStatusUseCase
import com.alilopez.kt_demohilt.features.user.presentation.screens.PaymentResult
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
    private val createCheckoutUseCase: CreateCheckoutUseCase,
    private val pollPaymentStatusUseCase: PollPaymentStatusUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PremiumUIState())
    val uiState: StateFlow<PremiumUIState> = _uiState.asStateFlow()

    fun startCheckout() {
        val userId = sessionManager.currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, paymentResult = null) }
            try {
                val checkout = createCheckoutUseCase(userId)
                _uiState.update { it.copy(isLoading = false, checkoutUrl = checkout.checkoutUrl) }
                pollPayment(checkout.preferenceId)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No pudimos conectar con el sistema de pagos. Intenta de nuevo."
                    )
                }
            }
        }
    }

    fun onCheckoutUrlConsumed() {
        _uiState.update { it.copy(checkoutUrl = null) }
    }

    private fun pollPayment(preferenceId: String) {
        val userId = sessionManager.currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isPolling = true) }
            pollPaymentStatusUseCase(preferenceId) { result ->
                when (result) {
                    PaymentPollResult.Approved -> {
                        viewModelScope.launch {
                            try {
                                val user = getUserUseCase(userId)
                                sessionManager.saveSession(
                                    userId = userId,
                                    token = sessionManager.accessToken ?: "",
                                    membership = user.membership
                                )
                            } catch (e: Exception) {
                                // Refresh success is best-effort
                            }
                            _uiState.update {
                                it.copy(
                                    isPolling = false,
                                    paymentResult = PaymentResult.APPROVED,
                                    isSuccess = true
                                )
                            }
                        }
                    }
                    PaymentPollResult.Rejected ->
                        _uiState.update { it.copy(isPolling = false, paymentResult = PaymentResult.REJECTED) }
                    PaymentPollResult.Timeout ->
                        _uiState.update { it.copy(isPolling = false, paymentResult = PaymentResult.TIMEOUT) }
                }
            }
        }
    }
}
