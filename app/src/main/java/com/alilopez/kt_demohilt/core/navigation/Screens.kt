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
