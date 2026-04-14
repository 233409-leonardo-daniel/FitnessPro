package com.alilopez.kt_demohilt.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object Register

@Serializable
object Home

@Serializable
object Recipes

@Serializable
object AddRecipe

@Serializable
data class RecipeDetail(val recipeId: Int)

@Serializable
data class EditRecipe(val recipeId: Int)

@Serializable
object Exercises

@Serializable
object AddExercise

@Serializable
data class EditExercise(val exerciseId: Int)

@Serializable
data class ExerciseDetail(val exerciseId: Int)

@Serializable
object WorkoutPlans

@Serializable
data class WorkoutDetail(val planId: Int, val planName: String)

@Serializable
object RecipePlans

@Serializable
data class RecipePlanDetail(val planId: Int, val planName: String)

@Serializable
data class Profile(val isOnboarding: Boolean = false)

@Serializable
object Premium

@Serializable
object Progression

@Serializable
object OfflineExercises
