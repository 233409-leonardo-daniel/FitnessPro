package com.alilopez.kt_demohilt.features.workoutplans.presentation.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alilopez.kt_demohilt.core.components.SearchBar
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.presentation.components.ExerciseCard
import com.alilopez.kt_demohilt.features.workoutplans.presentation.viewmodels.WorkoutDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    planId: Int,
    planName: String,
    onNavigateBack: () -> Unit,
    onNavigateToAddExercise: () -> Unit,
    viewModel: WorkoutDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()

    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val accentColor = Color(0xFF10B981)

    LaunchedEffect(planId) {
        viewModel.loadPlanExercises(planId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text(planName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = textColor)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadAvailableExercises() }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar existente", tint = accentColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = accentColor)
            } else if (uiState.exercises.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Esta lista está vacía", color = textColor, fontSize = 18.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.loadAvailableExercises() },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Text("Añadir ejercicio existente")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onNavigateToAddExercise,
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = SolidColor(accentColor))
                    ) {
                        Text("Crear ejercicio nuevo", color = accentColor)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(uiState.exercises) { exercise ->
                        ExerciseCard(
                            name = exercise.name,
                            imageUrl = exercise.gifUrl,
                            instructions = exercise.instructions,
                            isLocal = exercise.userId != null,
                            onRemoveClick = { 
                                val id = exercise.exerciseId ?: exercise.id?.toString() ?: ""
                                viewModel.removeExerciseFromPlan(planId, id) 
                            }
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(
                                onClick = { viewModel.loadAvailableExercises() },
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Añadir ejercicio existente")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = onNavigateToAddExercise) {
                                Text("¿No encuentras el ejercicio? Créalo aquí", color = accentColor)
                            }
                        }
                    }
                }
            }
        }

        if (uiState.isAddingExercise) {
            AddExerciseModal(
                exercises = uiState.availableLocalExercises,
                onDismiss = { viewModel.closeAddExercise() },
                onSelect = { exerciseId -> viewModel.addExerciseToPlan(planId, exerciseId) },
                isDarkTheme = isDarkTheme,
                accentColor = accentColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseModal(
    exercises: List<Exercise>,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
    isDarkTheme: Boolean,
    accentColor: Color
) {
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val secondaryTextColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
    var searchQuery by remember { mutableStateOf("") }

    val filteredExercises = remember(exercises, searchQuery) {
        val query = searchQuery.trim()
        if (query.isBlank()) {
            exercises
        } else {
            exercises.filter { it.name.contains(query, ignoreCase = true) }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                "Selecciona un ejercicio",
                modifier = Modifier.padding(16.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = {},
                onClear = { searchQuery = "" },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = "Buscar ejercicio por nombre",
                isDarkTheme = isDarkTheme,
                accentColor = accentColor,
                textColor = textColor,
                secondaryTextColor = secondaryTextColor
            )

            Spacer(modifier = Modifier.height(12.dp))
            
            if (exercises.isEmpty()) {
                Text(
                    "No tienes ejercicios locales disponibles para añadir.",
                    modifier = Modifier.padding(24.dp),
                    textAlign = TextAlign.Center
                )
            } else if (filteredExercises.isEmpty()) {
                Text(
                    "No se encontraron ejercicios con ese nombre.",
                    modifier = Modifier.padding(24.dp),
                    textAlign = TextAlign.Center,
                    color = secondaryTextColor
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxHeight(0.6f)) {
                    items(filteredExercises) { exercise ->
                        ListItem(
                            headlineContent = { Text(exercise.name) },
                            supportingContent = { Text(exercise.bodyparts.joinToString(", ")) },
                            trailingContent = {
                                Button(
                                    onClick = { 
                                        val id = exercise.exerciseId ?: exercise.id?.toString() ?: ""
                                        onSelect(id) 
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                                ) {
                                    Text("Añadir")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
