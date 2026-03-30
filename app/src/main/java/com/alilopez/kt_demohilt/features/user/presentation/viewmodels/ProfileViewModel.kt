package com.alilopez.kt_demohilt.features.user.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.features.user.domain.usecases.GetUserUseCase
import com.alilopez.kt_demohilt.features.user.domain.usecases.UpdateUserUseCase
import com.alilopez.kt_demohilt.features.user.presentation.screens.ProfileUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState> = _uiState.asStateFlow()

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

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val userId = sessionManager.currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val user = getUserUseCase(userId)
                _email.value = user.email
                _name.value = user.name
                _lastname.value = user.lastname
                _birthdate.value = user.birthdate ?: ""
                _weight.value = user.weight?.toString() ?: ""
                _height.value = user.height?.toString() ?: ""
                _gender.value = user.gender ?: ""
                _uiState.update { it.copy(isLoading = false, user = user) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onNameChange(value: String) { _name.value = value }
    fun onLastnameChange(value: String) { _lastname.value = value }
    fun onBirthdateChange(value: String) { _birthdate.value = value }
    fun onWeightChange(value: String) { _weight.value = value }
    fun onHeightChange(value: String) { _height.value = value }
    fun onGenderChange(value: String) { _gender.value = value }

    fun onSaveClick() {
        val userId = sessionManager.currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                updateUserUseCase(
                    id = userId,
                    email = _email.value,
                    name = _name.value,
                    lastname = _lastname.value,
                    birthdate = _birthdate.value,
                    weight = _weight.value.toDoubleOrNull(),
                    height = _height.value.toDoubleOrNull(),
                    gender = _gender.value,
                    membership = _uiState.value.user?.age?.toString() ?: "gratuito" // Using age as a placeholder for membership if not present
                )
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
