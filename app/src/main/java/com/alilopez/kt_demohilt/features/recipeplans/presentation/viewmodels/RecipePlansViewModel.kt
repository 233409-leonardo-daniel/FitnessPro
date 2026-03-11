package com.alilopez.kt_demohilt.features.recipeplans.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.features.recipeplans.domain.usecases.CreateRecipePlanUseCase
import com.alilopez.kt_demohilt.features.recipeplans.domain.usecases.DeleteRecipePlanUseCase
import com.alilopez.kt_demohilt.features.recipeplans.domain.usecases.GetUserRecipePlansUseCase
import com.alilopez.kt_demohilt.features.recipeplans.presentation.screens.RecipePlansUIState
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
    private val deleteRecipePlanUseCase: DeleteRecipePlanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipePlansUIState())
    val uiState: StateFlow<RecipePlansUIState> = _uiState.asStateFlow()

    init {
        loadRecipePlans()
    }

    fun loadRecipePlans() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val userId = 1 // TODO: Obtener del repositorio de usuario real
            
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
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            createRecipePlanUseCase(name, description, userId = 1, isPrivate).fold(
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
