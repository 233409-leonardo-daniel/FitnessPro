package com.alilopez.kt_demohilt.core.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.alilopez.kt_demohilt.features.exercise.presentation.screens.ExercisesScreen
import com.alilopez.kt_demohilt.features.exercise.presentation.screens.AddExerciseScreen
import com.alilopez.kt_demohilt.features.home.presentation.screens.HomeScreen
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.AddRecipeScreen
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.EditRecipeScreen
import com.alilopez.kt_demohilt.features.user.presentation.screens.LoginScreen
import com.alilopez.kt_demohilt.features.user.presentation.screens.RegisterScreen
import com.alilopez.kt_demohilt.features.home.presentation.components.SliderMenu
import com.alilopez.kt_demohilt.features.recipeplans.presentation.screens.RecipePlanDetailScreen
import com.alilopez.kt_demohilt.features.recipeplans.presentation.screens.RecipePlansScreen
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.RecipesScreen
import com.alilopez.kt_demohilt.features.workoutplans.presentation.screens.WorkoutPlansScreen
import com.alilopez.kt_demohilt.features.workoutplans.presentation.screens.WorkoutDetailScreen
import kotlinx.coroutines.launch

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    
    // Obtenemos el nombre simple de la ruta actual
    val currentRoute = navBackStackEntry?.destination?.route?.split(".")?.lastOrNull()

    // Rutas donde NO queremos habilitar gestos ni mostrar el SliderMenu
    val noDrawerRoutes = listOf("Login", "Register")
    val showDrawer = currentRoute != null && currentRoute !in noDrawerRoutes

    // EFECTO CLAVE: Forzar el cierre del menú cada vez que cambie la ruta
    LaunchedEffect(currentRoute) {
        if (drawerState.isOpen) {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showDrawer,
        drawerContent = {
            if (showDrawer) {
                SliderMenu(
                    onNavigateToHome = {
                        navController.navigate(Home) {
                            popUpTo(Home) { inclusive = true }
                        }
                    },
                    onNavigateToRecipes = {
                        navController.navigate(Recipes) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToExercises = {
                        navController.navigate(Exercises) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToWorkoutPlans = {
                        navController.navigate(WorkoutPlans) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToRecipePlans = {
                        navController.navigate(RecipePlans) {
                            launchSingleTop = true
                        }
                    },
                    currentRoute = currentRoute,
                    onCloseDrawer = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        NavHost(navController = navController, startDestination = Login) {
            composable<Login> {
                LoginScreen(
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

            composable<Register> {
                RegisterScreen(
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

            composable<Home> {
                HomeScreen(
                    onNavigateToRecipes = { navController.navigate(Recipes) },
                    onNavigateToExercises = { navController.navigate(Exercises) },
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable<Recipes> {
                RecipesScreen(
                    onNavigateToAddRecipe = { navController.navigate(AddRecipe) },
                    onNavigateToEditRecipe = { recipeId -> navController.navigate(EditRecipe(recipeId)) },
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable<AddRecipe> {
                AddRecipeScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable<EditRecipe> { backStackEntry ->
                val route = backStackEntry.toRoute<EditRecipe>()
                EditRecipeScreen(
                    recipeId = route.recipeId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<Exercises> {
                ExercisesScreen(
                    onNavigateToRecipes = {
                        navController.navigate(Recipes) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToAddExercise = {
                        navController.navigate(AddExercise)
                    },
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable<AddExercise> {
                AddExerciseScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable<WorkoutPlans> {
                WorkoutPlansScreen(
                    onNavigateToDetail = { planId, planName ->
                        navController.navigate(WorkoutDetail(planId, planName))
                    },
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable<WorkoutDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<WorkoutDetail>()
                WorkoutDetailScreen(
                    planId = route.planId,
                    planName = route.planName,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAddExercise = { navController.navigate(AddExercise) }
                )
            }

            composable<RecipePlans> {
                RecipePlansScreen(
                    onNavigateToDetail = { planId, planName ->
                        navController.navigate(RecipePlanDetail(planId, planName))
                    },
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable<RecipePlanDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<RecipePlanDetail>()
                RecipePlanDetailScreen(
                    planId = route.planId,
                    planName = route.planName,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAddRecipe = { navController.navigate(AddRecipe) }
                )
            }
        }
    }
}
