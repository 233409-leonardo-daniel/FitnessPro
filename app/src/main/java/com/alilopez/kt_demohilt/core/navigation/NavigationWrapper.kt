package com.alilopez.kt_demohilt.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.alilopez.kt_demohilt.core.components.StartIoBanner
import com.alilopez.kt_demohilt.features.exercise.presentation.screens.ExercisesScreen
import com.alilopez.kt_demohilt.features.exercise.presentation.screens.AddExerciseScreen
import com.alilopez.kt_demohilt.features.home.presentation.screens.HomeScreen
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.AddRecipeScreen
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.EditRecipeScreen
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.RecipeDetailScreen
import com.alilopez.kt_demohilt.features.user.presentation.screens.LoginScreen
import com.alilopez.kt_demohilt.features.user.presentation.screens.RegisterScreen
import com.alilopez.kt_demohilt.features.home.presentation.components.SliderMenu
import com.alilopez.kt_demohilt.features.recipeplans.presentation.screens.RecipePlanDetailScreen
import com.alilopez.kt_demohilt.features.recipeplans.presentation.screens.RecipePlansScreen
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.RecipesScreen
import com.alilopez.kt_demohilt.features.user.presentation.screens.ProfileScreen
import com.alilopez.kt_demohilt.features.workoutplans.presentation.screens.WorkoutPlansScreen
import com.alilopez.kt_demohilt.features.workoutplans.presentation.screens.WorkoutDetailScreen
import kotlinx.coroutines.launch

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route?.split(".")?.lastOrNull()

    LaunchedEffect(currentDestination?.route) {
        drawerState.close()
    }

    val isLoginRoute = currentDestination?.hasRoute<Login>() ?: true
    val isAuthRoute = currentDestination?.let {
        it.hasRoute<Login>() || it.hasRoute<Register>() || it.hasRoute<Profile>()
    } ?: true
    val showDrawer = !isAuthRoute

    Scaffold(
        bottomBar = {
            // Mostrar banner en todas las pantallas excepto en Login
            if (!isLoginRoute) {
                StartIoBanner()
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
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
                            onNavigateToProfile = {
                                navController.navigate(Profile(isOnboarding = false)) {
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
                            onNavigateToProfileOnboarding = {
                                navController.navigate(Profile(isOnboarding = true)) {
                                    popUpTo(Home) { inclusive = false }
                                }
                            },
                            onOpenDrawer = { scope.launch { drawerState.open() } }
                        )
                    }

                    composable<Profile> { backStackEntry ->
                        val route = backStackEntry.toRoute<Profile>()
                        ProfileScreen(
                            isOnboarding = route.isOnboarding,
                            onNavigateBack = { navController.popBackStack() },
                            onSaveSuccess = {
                                if (route.isOnboarding) {
                                    navController.navigate(Home) {
                                        popUpTo(Profile::class) { inclusive = true }
                                    }
                                } else {
                                    navController.popBackStack()
                                }
                            }
                        )
                    }

                    composable<Recipes> {
                        RecipesScreen(
                            onNavigateToAddRecipe = { navController.navigate(AddRecipe) },
                            onNavigateToEditRecipe = { recipeId -> navController.navigate(EditRecipe(recipeId)) },
                            onNavigateToRecipeDetail = { recipeId -> navController.navigate(RecipeDetail(recipeId)) },
                            onOpenDrawer = { scope.launch { drawerState.open() } }
                        )
                    }

                    composable<AddRecipe> {
                        AddRecipeScreen(onNavigateBack = { navController.popBackStack() })
                    }

                    composable<RecipeDetail> { backStackEntry ->
                        val route = backStackEntry.toRoute<RecipeDetail>()
                        RecipeDetailScreen(
                            recipeId = route.recipeId,
                            onNavigateBack = { navController.popBackStack() }
                        )
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
    }
}
