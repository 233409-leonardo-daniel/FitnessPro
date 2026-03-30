package com.alilopez.kt_demohilt.features.recipeplans.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.features.recipeplans.domain.usecases.CreateRecipePlanUseCase
import com.alilopez.kt_demohilt.features.recipeplans.domain.usecases.DeleteRecipePlanUseCase
import com.alilopez.kt_demohilt.features.recipeplans.domain.usecases.GetUserRecipePlansUseCase
import com.alilopez.kt_demohilt.features.recipeplans.presentation.screens.RecipePlansUIState
import com.alilopez.kt_demohilt.core.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipePlansViewModel @Inject constructor(
    private val getUserRecipePlansUseCase: GetUserRecipePlansUseCase,
    private val createRecipePlanUseCase: CreateRecipePlanUseCase,
    private val deleteRecipePlanUseCase: DeleteRecipePlanUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipePlansUIState())
    val uiState: StateFlow<RecipePlansUIState> = _uiState.asStateFlow()

    init {
        loadRecipePlans()
    }

    fun loadRecipePlans() {
        val userId = sessionManager.currentUserId
        
        // Validar que el usuario esté autenticado
        if (userId == null) {
            _uiState.update { 
                it.copy(isLoading = false, errorMessage = "Usuario no autenticado", recipePlans = emptyList()) 
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getUserRecipePlansUseCase(userId).fold(
                onSuccess = { plans ->
                    _uiState.update { it.copy(isLoading = false, recipePlans = plans) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun createPlan(name: String, description: String, isPrivate: Boolean) {
        val userId = sessionManager.currentUserId
        
        // Validar que el usuario esté autenticado
        if (userId == null) {
            _uiState.update { 
                it.copy(errorMessage = "Usuario no autenticado") 
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            createRecipePlanUseCase(name, description, userId = userId, isPrivate).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, planCreated = true) }
                    loadRecipePlans()
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun deletePlan(planId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            deleteRecipePlanUseCase(planId).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, planDeleted = true) }
                    loadRecipePlans()
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun resetStatus() {
        _uiState.update { it.copy(planCreated = false, planDeleted = false) }
    }
}
