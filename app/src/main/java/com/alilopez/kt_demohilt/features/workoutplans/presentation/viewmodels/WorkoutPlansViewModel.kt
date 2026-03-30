package com.alilopez.kt_demohilt.features.workoutplans.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.features.user.domain.repositories.UserRepository
import com.alilopez.kt_demohilt.features.workoutplans.domain.usecases.CreateWorkoutPlanUseCase
import com.alilopez.kt_demohilt.features.workoutplans.domain.usecases.DeleteWorkoutPlanUseCase
import com.alilopez.kt_demohilt.features.workoutplans.domain.usecases.GetUserWorkoutPlansUseCase
import com.alilopez.kt_demohilt.features.workoutplans.presentation.screens.WorkoutPlansUIState
import com.alilopez.kt_demohilt.core.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutPlansViewModel @Inject constructor(
    private val getUserWorkoutPlansUseCase: GetUserWorkoutPlansUseCase,
    private val createWorkoutPlanUseCase: CreateWorkoutPlanUseCase,
    private val deleteWorkoutPlanUseCase: DeleteWorkoutPlanUseCase,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutPlansUIState())
    val uiState: StateFlow<WorkoutPlansUIState> = _uiState.asStateFlow()

    init {
        loadWorkoutPlans()
    }

    fun loadWorkoutPlans() {
        val userId = sessionManager.currentUserId
        
        // Validar que el usuario esté autenticado
        if (userId == null) {
            _uiState.update { 
                it.copy(isLoading = false, errorMessage = "Usuario no autenticado", workoutPlans = emptyList()) 
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            getUserWorkoutPlansUseCase(userId).fold(
                onSuccess = { plans ->
                    _uiState.update { it.copy(isLoading = false, workoutPlans = plans) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun createPlan(name: String, description: String, planType: String, isPrivate: Boolean) {
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
            createWorkoutPlanUseCase(name, description, userId = userId, planType, isPrivate).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, planCreated = true) }
                    loadWorkoutPlans()
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
            deleteWorkoutPlanUseCase(planId).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, planDeleted = true) }
                    loadWorkoutPlans()
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
