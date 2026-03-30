package com.alilopez.kt_demohilt.features.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.features.home.presentation.screens.HomeUIState
import com.alilopez.kt_demohilt.features.user.domain.usecases.GetUserDailyContentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserDailyContentUseCase: GetUserDailyContentUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    val currentUserId: Int? get() = sessionManager.currentUserId

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val userId = currentUserId
            if (userId == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No hay una sesion activa"
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val dailyContent = getUserDailyContentUseCase(userId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        day = dailyContent.day,
                        timezone = dailyContent.timezone,
                        userFullName = "${dailyContent.userName} ${dailyContent.userLastname}".trim(),
                        recipes = dailyContent.recipes,
                        exercises = dailyContent.exercises
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al cargar tu plan diario"
                    )
                }
            }
        }
    }
}
