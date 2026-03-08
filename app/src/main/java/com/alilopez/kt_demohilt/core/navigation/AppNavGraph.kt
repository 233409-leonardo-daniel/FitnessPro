package com.alilopez.kt_demohilt.core.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.alilopez.kt_demohilt.features.exercise.presentation.screens.ExercisesScreen
import com.alilopez.kt_demohilt.features.recipies.presentation.HomeScreen
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.AddRecipeScreen
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.EditRecipeScreen
import com.alilopez.kt_demohilt.features.user.presentation.screens.LoginScreen
import com.alilopez.kt_demohilt.features.user.presentation.screens.RegisterScreen
import com.alilopez.kt_demohilt.features.user.presentation.viewmodels.LoginViewModel
import com.alilopez.kt_demohilt.features.user.presentation.viewmodels.RegisterViewModel
import javax.inject.Inject

class AppNavGraph @Inject constructor() : FeatureNavGraph {

    override fun registerNavGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController
    ) {
        // Rutas de Usuario
        navGraphBuilder.composable<Login> {
            val viewModel: LoginViewModel = hiltViewModel()

            LoginScreen(
                viewModel = viewModel,
                onClickLogin = {
                    navController.navigate(Home) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Register)
                }
            )
        }

        navGraphBuilder.composable<Register> {
            val viewModel: RegisterViewModel = hiltViewModel()

            RegisterScreen(
                viewModel = viewModel,
                onRegisterSuccess = {
                    navController.navigate(Login) {
                        popUpTo(Register) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Login) {
                        popUpTo(Register) { inclusive = true }
                    }
                }
            )
        }

        // Rutas de Recetas (Home es la lista de recetas)
        navGraphBuilder.composable<Home> {
            HomeScreen(
                onNavigateToAddRecipe = {
                    navController.navigate(AddRecipe)
                },
                onNavigateToEditRecipe = { recipeId ->
                    navController.navigate(EditRecipe(recipeId = recipeId))
                },
                onNavigateToExercises = {
                    navController.navigate(Exercises)
                }
            )
        }

        navGraphBuilder.composable<AddRecipe> {
            AddRecipeScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        navGraphBuilder.composable<EditRecipe> { backStackEntry ->
            val editRecipe = backStackEntry.toRoute<EditRecipe>()
            EditRecipeScreen(
                recipeId = editRecipe.recipeId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Rutas de Ejercicios
        navGraphBuilder.composable<Exercises> {
            ExercisesScreen(
                onNavigateToRecipes = {
                    navController.navigate(Home) {
                        popUpTo(Home) { inclusive = true }
                    }
                }
            )
        }
    }
}
