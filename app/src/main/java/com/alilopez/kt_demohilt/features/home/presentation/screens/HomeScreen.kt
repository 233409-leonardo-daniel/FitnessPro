package com.alilopez.kt_demohilt.features.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alilopez.kt_demohilt.features.exercise.presentation.components.ExerciseCard
import com.alilopez.kt_demohilt.features.home.presentation.viewmodels.HomeViewModel
import com.alilopez.kt_demohilt.features.recipies.presentation.components.RecipeCard
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToRecipes: () -> Unit,
    onNavigateToExercises: () -> Unit,
    onNavigateToProfileOnboarding: () -> Unit,
    onOpenDrawer: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Usar colores directamente del MaterialTheme para consistencia
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val recipesAccentColor = Color(0xFF10B981)
    val trainingAccentColor = Color(0xFFF59E0B)

    val currentDay = uiState.day.ifBlank { "Hoy" }
    val todaysRecipes = uiState.recipes
    val todaysExercises = uiState.exercises

    // Redirigir a completar perfil si es necesario
    LaunchedEffect(uiState.isProfileIncomplete) {
        if (uiState.isProfileIncomplete) {
            onNavigateToProfileOnboarding()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mi Plan Diario",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = textColor
                        )
                        Text(
                            text = currentDay,
                            fontSize = 14.sp,
                            color = recipesAccentColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Abrir menú",
                            tint = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = viewModel::loadData,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {

            uiState.errorMessage?.let { message ->
                item {
                    EmptyDayCard(
                        title = "No se pudo cargar tu plan diario",
                        subtitle = message
                    )
                }
            }

            // ── SECCIÓN RECETAS DE HOY (CARRUSEL) ──
            item {
                SectionHeader(
                    title = "Comidas programadas",
                    onSeeAllClick = onNavigateToRecipes,
                    textColor = textColor,
                    accentColor = recipesAccentColor,
                    indicatorColor = recipesAccentColor
                )
            }

            item {
                if (todaysRecipes.isEmpty()) {
                    EmptyDayCard("No tienes comidas para hoy", "Agrega días a tus recetas para verlas aquí")
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(todaysRecipes) { recipe ->
                            RecipeCard(
                                recipe = recipe,
                                compactMode = true,
                                modifier = Modifier.width(300.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = "Tu entrenamiento",
                    onSeeAllClick = onNavigateToExercises,
                    textColor = textColor,
                    accentColor = trainingAccentColor,
                    indicatorColor = trainingAccentColor
                )
            }

            item {
                if (todaysExercises.isEmpty()) {
                    EmptyDayCard("Día de descanso", "No hay ejercicios programados para hoy")
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(todaysExercises) { exercise ->
                            ExerciseCard(
                                name = exercise.name,
                                imageUrl = exercise.gifUrl,
                                compactMode = true,
                                instructions = exercise.instructions,
                                exerciseType = exercise.exerciseType,
                                difficulty = exercise.difficulty,
                                accentColor = trainingAccentColor,
                                modifier = Modifier.width(300.dp)
                            )
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit,
    textColor: Color,
    accentColor: Color,
    indicatorColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(20.dp)
                    .background(indicatorColor, RoundedCornerShape(8.dp))
            )
            Text(
                text = title.uppercase(Locale.getDefault()),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
        }
        TextButton(onClick = onSeeAllClick) {
            Text("Ver todo", color = accentColor, fontSize = 14.sp)
        }
    }
}

@Composable
private fun EmptyDayCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}
