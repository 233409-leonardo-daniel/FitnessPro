package com.alilopez.kt_demohilt.features.home.presentation.screens

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe
import com.alilopez.kt_demohilt.features.user.domain.entities.User

data class HomeUIState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val recipes: List<Recipe> = emptyList(),
    val exercises: List<Exercise> = emptyList(),
    val recipesLoading: Boolean = false,
    val exercisesLoading: Boolean = false,
    val recipesError: String? = null,
    val exercisesError: String? = null,
    val isProfileIncomplete: Boolean = false,
    val currentUser: User? = null
)
