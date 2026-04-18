package com.alilopez.kt_demohilt.features.progression.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.features.progression.domain.usecases.AddProgressionEntryUseCase
import com.alilopez.kt_demohilt.features.progression.domain.usecases.GetProgressionHistoryUseCase
import com.alilopez.kt_demohilt.features.progression.domain.usecases.GetProgressionSummaryUseCase
import com.alilopez.kt_demohilt.features.progression.presentation.screens.ProgressionUIState
import com.alilopez.kt_demohilt.features.user.domain.usecases.GetUserUseCase
import com.alilopez.kt_demohilt.features.user.domain.usecases.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressionViewModel @Inject constructor(
    private val getProgressionHistoryUseCase: GetProgressionHistoryUseCase,
    private val getProgressionSummaryUseCase: GetProgressionSummaryUseCase,
    private val addProgressionEntryUseCase: AddProgressionEntryUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressionUIState())
    val uiState: StateFlow<ProgressionUIState> = _uiState.asStateFlow()

    init {
        loadProgressionData()
    }

    fun loadProgressionData() {
        val userId = sessionManager.currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val summary = getProgressionSummaryUseCase(userId)
                val history = getProgressionHistoryUseCase(userId)
                _uiState.update { it.copy(
                    isLoading = false,
                    summary = summary,
                    history = history
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun addWeightEntry(weight: Float) {
        val userId = sessionManager.currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                addProgressionEntryUseCase(userId, weight)
                loadProgressionData()
                _uiState.update { it.copy(isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
    
    fun updateTargetWeight(newTarget: Float) {
        val userId = sessionManager.currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val user = getUserUseCase(userId)
                updateUserUseCase(
                    id = userId,
                    email = user.email,
                    name = user.name,
                    lastname = user.lastname,
                    birthdate = user.birthdate,
                    weight = user.weight,
                    height = user.height,
                    gender = user.gender,
                    membership = user.membership,
                    targetWeight = newTarget
                )
                loadProgressionData()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
