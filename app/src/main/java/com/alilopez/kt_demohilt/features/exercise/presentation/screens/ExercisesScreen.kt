package com.alilopez.kt_demohilt.features.exercise.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alilopez.kt_demohilt.features.exercise.presentation.viewmodels.ExerciseViewModel
import com.alilopez.kt_demohilt.features.exercise.presentation.components.ExerciseCard

val bodyParts = listOf(
    "ESPALDA",
    "PANTORRILLAS",
    "PECHO",
    "ANTEBRAZOS",
    "CADERAS",
    "CUELLO",
    "HOMBROS",
    "MUSLOS",
    "CINTURA",
    "PIERNAS",
    "MANOS",
    "PIES",
    "CARA",
    "CUERPO COMPLETO",
    "BÍCEPS",
    "BRAZOS",
    "TRÍCEPS",
    "ISQUIOTIBIALES",
    "GLÚTEOS",
    "CUÁDRICEPS"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(
    onNavigateToRecipes: () -> Unit,
    onNavigateToAddExercise: () -> Unit,
    onOpenDrawer: () -> Unit,
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()

    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val topBarColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val secondaryTextColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
    val accentColor = Color(0xFF10B981)
    val dropdownBackground = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val clearButtonColor = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)

//    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
//        viewModel.syncExercises()
//    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("FitnessPro", fontWeight = FontWeight.ExtraBold, color = textColor) },
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
                    IconButton(onClick = { viewModel.loadExercises() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Exercises",
                            tint = accentColor
                        )
                    }
                    IconButton(onClick = onNavigateToAddExercise) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Exercise",
                            tint = accentColor
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = topBarColor
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = accentColor
                    )
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: "Error",
                        modifier = Modifier.align(Alignment.Center),
                        color = accentColor
                    )
                }
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { viewModel.toggleFilter() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = accentColor
                                    )
                                ) {
                                    Text("Filter body parts", color = Color.White)
                                }

                                if (uiState.selectedBodyPart != null) {
                                    Spacer(Modifier.width(8.dp))
                                    Button(
                                        onClick = { viewModel.clearFilters() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = clearButtonColor
                                        )
                                    ) {
                                        @Suppress("DEPRECATION")
                                        Text("Clear", color = textColor)
                                    }
                                }
                            }

                            DropdownMenu(
                                expanded = uiState.isFilterExpanded,
                                onDismissRequest = { viewModel.toggleFilter() },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .background(dropdownBackground)
                            ) {
                                bodyParts.forEach { bodyPart ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Checkbox(
                                                    checked = uiState.selectedBodyPart == bodyPart,
                                                    onCheckedChange = {
                                                        viewModel.onBodyPartChecked(bodyPart, it)
                                                    },
                                                    colors = CheckboxDefaults.colors(
                                                        checkedColor = accentColor,
                                                        uncheckedColor = secondaryTextColor
                                                    )
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text(bodyPart.lowercase().replaceFirstChar { it.uppercase() }, color = textColor)
                                            }
                                        },
                                        onClick = {
                                            val isCurrentlySelected = uiState.selectedBodyPart == bodyPart
                                            viewModel.onBodyPartChecked(bodyPart, !isCurrentlySelected)
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.applyFilters() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = accentColor
                                    )
                                ) {
                                    Text("Apply filters", color = Color.White)
                                }
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            if (uiState.localExercises.isNotEmpty() && !uiState.isFiltered) {
                                item {
                                    Text(
                                        text = "Mis Ejercicios",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
                                    )
                                }
                                items(uiState.localExercises) { exercise ->
                                    ExerciseCard(
                                        name = exercise.name,
                                        imageUrl = exercise.gifUrl,
                                        instructions = exercise.instructions,
                                        isLocal = true,
                                        exerciseType = exercise.exerciseType,
                                        difficulty = exercise.difficulty
                                    )
                                }
                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            item {
                                Text(
                                    text = if (uiState.isFiltered) "Resultados del filtro" else "",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
                                )
                            }
                            items(uiState.exercises) { exercise ->
                                ExerciseCard(
                                    name = exercise.name,
                                    imageUrl = exercise.gifUrl,
                                    instructions = exercise.instructions
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ExercisesScreenPreview() {
    Text(text = "Exercises Screen Preview")
}
