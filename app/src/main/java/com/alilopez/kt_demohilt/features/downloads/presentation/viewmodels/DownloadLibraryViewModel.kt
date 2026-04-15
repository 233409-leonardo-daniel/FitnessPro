package com.alilopez.kt_demohilt.features.downloads.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.features.downloads.presentation.screens.DownloadLibraryUIState
import com.alilopez.kt_demohilt.features.recipeplans.domain.usecases.GetDownloadedRecipePlansUseCase
import com.alilopez.kt_demohilt.features.workoutplans.domain.usecases.GetDownloadedWorkoutPlansUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadLibraryViewModel @Inject constructor(
    private val getDownloadedRecipePlansUseCase: GetDownloadedRecipePlansUseCase,
    private val getDownloadedWorkoutPlansUseCase: GetDownloadedWorkoutPlansUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DownloadLibraryUIState())
    val uiState: StateFlow<DownloadLibraryUIState> = _uiState.asStateFlow()

    init {
        loadDownloadedContent()
    }

    private fun loadDownloadedContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            combine(
                getDownloadedRecipePlansUseCase(),
                getDownloadedWorkoutPlansUseCase()
            ) { recipes, workouts ->
                DownloadLibraryUIState(
                    isLoading = false,
                    downloadedRecipes = recipes,
                    downloadedWorkouts = workouts
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
}
