package com.alilopez.kt_demohilt.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object Register

@Serializable
object Home

@Serializable
object AddRecipe

@Serializable
data class EditRecipe(val recipeId: Int)

@Serializable
object Exercises

@Serializable
object AddExercise

@Serializable
object WorkoutPlans

@Serializable
data class WorkoutDetail(val planId: Int, val planName: String)

@Serializable
object RecipePlans

@Serializable
data class RecipePlanDetail(val planId: Int, val planName: String)
