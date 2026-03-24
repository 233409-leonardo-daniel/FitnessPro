package com.alilopez.kt_demohilt.features.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.GetLocalExercisesUseCase
import com.alilopez.kt_demohilt.features.home.presentation.screens.HomeUIState
import com.alilopez.kt_demohilt.features.recipies.domain.usecases.GetRecipesUseCase
import com.alilopez.kt_demohilt.features.user.domain.usecases.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val getLocalExercisesUseCase: GetLocalExercisesUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    val currentUserId: Int? get() = sessionManager.currentUserId

    init {
        loadData()
        checkUserProfile()
    }

    fun loadData() {
        loadRecipes()
        loadLocalExercises()
    }

    private fun checkUserProfile() {
        viewModelScope.launch {
            currentUserId?.let { id ->
                try {
                    val user = getUserUseCase(id)
                    val isIncomplete = user.birthdate.isNullOrBlank() || 
                                     user.weight == null || user.weight == 0.0 ||
                                     user.height == null || user.height == 0.0
                    
                    _uiState.update { it.copy(
                        isProfileIncomplete = isIncomplete,
                        currentUser = user
                    ) }
                } catch (e: Exception) {
                    // Ignorar error de perfil por ahora
                }
            }
        }
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            _uiState.update { it.copy(recipesLoading = true, recipesError = null) }
            try {
                val recipes = getRecipesUseCase()
                _uiState.update { it.copy(recipesLoading = false, recipes = recipes) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(recipesLoading = false, recipesError = e.message ?: "Error al cargar recetas")
                }
            }
        }
    }

    private fun loadLocalExercises() {
        _uiState.update { it.copy(exercisesLoading = true) }
        
        val today = getCurrentDayOfWeek()
        
        getLocalExercisesUseCase().onEach { allLocalExercises ->
            val exercisesForToday = allLocalExercises.filter { exercise ->
                exercise.scheduledDays.any { it.equals(today, ignoreCase = true) }
            }
            
            _uiState.update { 
                it.copy(
                    exercisesLoading = false, 
                    exercises = exercisesForToday 
                ) 
            }
        }.launchIn(viewModelScope)
    }

    private fun getCurrentDayOfWeek(): String {
        val sdf = SimpleDateFormat("EEEE", Locale("es", "ES"))
        val d = Calendar.getInstance().time
        return sdf.format(d)
    }
}
