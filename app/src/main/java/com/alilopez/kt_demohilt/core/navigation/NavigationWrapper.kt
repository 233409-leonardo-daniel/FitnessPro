package com.alilopez.kt_demohilt.core.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.alilopez.kt_demohilt.core.components.StartIoBanner
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.features.exercise.presentation.screens.AddExerciseScreen
import com.alilopez.kt_demohilt.features.exercise.presentation.screens.ExercisesScreen
import com.alilopez.kt_demohilt.features.home.presentation.components.SliderMenu
import com.alilopez.kt_demohilt.features.home.presentation.screens.HomeScreen
import com.alilopez.kt_demohilt.features.progression.presentation.screens.ProgressionScreen
import com.alilopez.kt_demohilt.features.recipeplans.presentation.screens.RecipePlanDetailScreen
import com.alilopez.kt_demohilt.features.recipeplans.presentation.screens.RecipePlansScreen
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.AddRecipeScreen
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.EditRecipeScreen
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.RecipeDetailScreen
import com.alilopez.kt_demohilt.features.recipies.presentation.screens.RecipesScreen
import com.alilopez.kt_demohilt.features.user.presentation.screens.LoginScreen
import com.alilopez.kt_demohilt.features.user.presentation.screens.PremiumScreen
import com.alilopez.kt_demohilt.features.user.presentation.screens.ProfileScreen
import com.alilopez.kt_demohilt.features.user.presentation.screens.RegisterScreen
import com.alilopez.kt_demohilt.features.user.presentation.screens.TermsAndConditionsDialog
import com.alilopez.kt_demohilt.features.workoutplans.presentation.screens.WorkoutDetailScreen
import com.alilopez.kt_demohilt.features.workoutplans.presentation.screens.WorkoutPlansScreen
import kotlinx.coroutines.launch

@Composable
fun NavigationWrapper(
    sessionManager: SessionManager
) {
    val navController = rememberNavController()
    val activity = LocalActivity.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showTermsDialog by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route?.split(".")?.lastOrNull()

    val membership by sessionManager.membership.collectAsStateWithLifecycle()

    LaunchedEffect(currentDestination?.route) {
        drawerState.close()
    }

    LaunchedEffect(currentDestination?.route, sessionManager.currentUserId) {
        val shouldValidateTerms = currentDestination?.let {
            !it.hasRoute<Login>() && !it.hasRoute<Register>()
        } ?: false

        showTermsDialog = shouldValidateTerms &&
            sessionManager.isLoggedIn() &&
            !sessionManager.hasAcceptedCurrentTerms()
    }

    val isLoginRoute = currentDestination?.hasRoute<Login>() ?: true
    val isAuthRoute = currentDestination?.let {
        it.hasRoute<Login>() || it.hasRoute<Register>() || it.hasRoute<Profile>() || it.hasRoute<Premium>()
    } ?: true
    val showDrawer = !isAuthRoute

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            if (!isLoginRoute && membership == "gratuito") {
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
                            onNavigateToProgression = {
                                navController.navigate(Progression) {
                                    launchSingleTop = true
                                }
                            },
                            onNavigateToProfile = {
                                navController.navigate(Profile(isOnboarding = false)) {
                                    launchSingleTop = true
                                }
                            },
                            onNavigateToPremium = {
                                navController.navigate(Premium) {
                                    launchSingleTop = true
                                }
                            },
                            isPremium = membership != null && membership != "gratuito",
                            onLogout = {
                                sessionManager.clearSession()
                                navController.navigate(Login) {
                                    popUpTo(0) { inclusive = true }
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

                    composable<Premium> {
                        PremiumScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onSuccess = {
                                navController.navigate(Home) {
                                    popUpTo(Premium) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable<Recipes> {
                        RecipesScreen(
                            onNavigateToAddRecipe = { navController.navigate(AddRecipe) },
                            onNavigateToEditRecipe = { recipeId -> navController.navigate(EditRecipe(recipeId)) },
                            onNavigateToRecipeDetail = { recipeId -> navController.navigate(RecipeDetail(recipeId)) },
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            membership = membership,
                            onNavigateToPremium = { navController.navigate(Premium) { launchSingleTop = true } }
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

                    composable<Exercises> {
                        ExercisesScreen(
                            onNavigateToAddExercise = {
                                navController.navigate(AddExercise)
                            },
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            membership = membership,
                            onNavigateToPremium = { navController.navigate(Premium) { launchSingleTop = true } }
                        )
                    }

                composable<EditRecipe> { backStackEntry ->
                    val route = backStackEntry.toRoute<EditRecipe>()
                    EditRecipeScreen(
                        recipeId = route.recipeId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                    composable<AddExercise> {
                        AddExerciseScreen(onNavigateBack = { navController.popBackStack() })
                    }

                    composable<Progression> {
                        ProgressionScreen(
                            onOpenDrawer = { scope.launch { drawerState.open() } }
                        )
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

            if (showTermsDialog) {
                TermsAndConditionsDialog(
                    onAccept = {
                        sessionManager.acceptCurrentTerms()
                        showTermsDialog = false
                    },
                    onReject = {
                        sessionManager.clearSession()
                        showTermsDialog = false
                        navController.navigate(Login) {
                            popUpTo(0) { inclusive = true }
                        }
                        activity?.finishAffinity()
                    }
                )
            }
        }
    }
}
