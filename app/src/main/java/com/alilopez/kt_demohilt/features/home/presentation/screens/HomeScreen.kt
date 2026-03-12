package com.alilopez.kt_demohilt.features.home.presentation.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.presentation.components.gifImageLoader
import com.alilopez.kt_demohilt.features.home.presentation.viewmodels.HomeViewModel
import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe
import com.alilopez.kt_demohilt.features.recipies.presentation.components.RecipeCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToRecipes: () -> Unit,
    onNavigateToExercises: () -> Unit,
    onOpenDrawer: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()

    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val secondaryTextColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
    val accentColor = Color(0xFF10B981)

    // Obtener día actual en español
    val calendar = Calendar.getInstance()
    val dayFormat = SimpleDateFormat("EEEE", Locale("es", "ES"))
    val currentDay = dayFormat.format(calendar.time)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    // Filtrar recetas y ejercicios por el día de hoy
    val todaysRecipes = uiState.recipes.filter { recipe ->
        recipe.scheduledDays.any { it.equals(currentDay, ignoreCase = true) }
    }
    
    val todaysExercises = uiState.exercises.filter { exercise ->
        exercise.scheduledDays.any { it.equals(currentDay, ignoreCase = true) }
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
                            color = accentColor,
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
                actions = {
                    IconButton(onClick = { viewModel.loadData() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar",
                            tint = accentColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ── SECCIÓN RECETAS DE HOY (CARRUSEL) ──
            item {
                SectionHeader(
                    title = "Comidas programadas",
                    onSeeAllClick = onNavigateToRecipes,
                    textColor = textColor,
                    accentColor = accentColor
                )
            }

            item {
                if (todaysRecipes.isEmpty()) {
                    EmptyDayCard("No tienes comidas para hoy", "Agrega días a tus recetas para verlas aquí", isDarkTheme)
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(todaysRecipes) { recipe ->
                            RecipeCard(
                                recipe = recipe,
                                modifier = Modifier.width(300.dp) // Ancho fijo para el carrusel
                            )
                        }
                    }
                }
            }

            // ── SECCIÓN EJERCICIOS DE HOY (CARRUSEL) ──
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = "Tu entrenamiento",
                    onSeeAllClick = onNavigateToExercises,
                    textColor = textColor,
                    accentColor = accentColor
                )
            }

            item {
                if (todaysExercises.isEmpty()) {
                    EmptyDayCard("Día de descanso", "No hay ejercicios programados para hoy", isDarkTheme)
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(todaysExercises) { exercise ->
                            HomeExerciseCard(
                                exercise = exercise,
                                isDarkTheme = isDarkTheme,
                                modifier = Modifier.width(280.dp) // Ancho fijo para el carrusel
                            )
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
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        TextButton(onClick = onSeeAllClick) {
            Text("Ver todo", color = accentColor, fontSize = 14.sp)
        }
    }
}

@Composable
private fun EmptyDayCard(title: String, subtitle: String, isDarkTheme: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontWeight = FontWeight.Bold, color = if (isDarkTheme) Color.White else Color.Black)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

// ── Card compacta de ejercicio para Home ──
@Composable
private fun HomeExerciseCard(
    exercise: Exercise,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val secondaryTextColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
    val accentColor = Color(0xFF10B981)

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = exercise.gifUrl,
                imageLoader = gifImageLoader(LocalContext.current),
                contentDescription = exercise.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name.replaceFirstChar { it.uppercase() },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = exercise.instructions.firstOrNull() ?: "",
                    fontSize = 13.sp,
                    color = secondaryTextColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
