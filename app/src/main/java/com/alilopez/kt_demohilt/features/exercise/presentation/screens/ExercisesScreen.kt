package com.alilopez.kt_demohilt.features.exercise.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alilopez.kt_demohilt.features.exercise.presentation.viewmodels.ExerciseViewModel
import com.alilopez.kt_demohilt.features.exercise.presentation.components.ExerciseCard
import kotlinx.coroutines.launch

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
    val accentColor = Color(0xFF10B981)
    val dropdownBackground = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val clearButtonColor = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)

    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text("FitnessPro", fontWeight = FontWeight.ExtraBold, color = textColor) },
                    navigationIcon = {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Abrir menú", tint = textColor)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.loadExercises() }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = accentColor)
                        }
                        IconButton(onClick = onNavigateToAddExercise) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Exercise", tint = accentColor)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = topBarColor)
                )
                
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = topBarColor,
                    contentColor = accentColor,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            color = accentColor
                        )
                    }
                ) {
                    Tab(
                        selected = pagerState.currentPage == 0,
                        onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                        text = { Text("Mis Ejercicios", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = pagerState.currentPage == 1,
                        onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                        text = { Text("Explorar", fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top
            ) { page ->
                when (page) {
                    0 -> LocalExercisesList(uiState.localExercises, textColor)
                    1 -> ExploreExercisesList(
                        exercises = uiState.exercises,
                        uiState = uiState,
                        viewModel = viewModel,
                        textColor = textColor,
                        accentColor = accentColor,
                        clearButtonColor = clearButtonColor,
                        dropdownBackground = dropdownBackground
                    )
                }
            }
        }
    }
}

@Composable
fun LocalExercisesList(localExercises: List<com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise>, textColor: Color) {
    if (localExercises.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No has creado ejercicios aún", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(localExercises) { exercise ->
                ExerciseCard(
                    name = exercise.name,
                    imageUrl = exercise.gifUrl,
                    instructions = exercise.instructions,
                    isLocal = true,
                    exerciseType = exercise.exerciseType,
                    difficulty = exercise.difficulty
                )
            }
        }
    }
}

@Composable
fun ExploreExercisesList(
    exercises: List<com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise>,
    uiState: com.alilopez.kt_demohilt.features.exercise.presentation.screens.ExercisesUiState,
    viewModel: ExerciseViewModel,
    textColor: Color,
    accentColor: Color,
    clearButtonColor: Color,
    dropdownBackground: Color
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Filtros (solo para explorar)
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { viewModel.toggleFilter() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text("Filter body parts", color = Color.White)
                }

                if (uiState.selectedBodyPart != null) {
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.clearFilters() },
                        colors = ButtonDefaults.buttonColors(containerColor = clearButtonColor)
                    ) {
                        Text("Clear", color = textColor)
                    }
                }
            }

            DropdownMenu(
                expanded = uiState.isFilterExpanded,
                onDismissRequest = { viewModel.toggleFilter() },
                modifier = Modifier.fillMaxWidth(0.8f).background(dropdownBackground)
            ) {
                bodyParts.forEach { bodyPart ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = uiState.selectedBodyPart == bodyPart,
                                    onCheckedChange = { viewModel.onBodyPartChecked(bodyPart, it) },
                                    colors = CheckboxDefaults.colors(checkedColor = accentColor)
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
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text("Apply filters", color = Color.White)
                }
            }
        }

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accentColor)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(exercises) { exercise ->
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

@Preview
@Composable
fun ExercisesScreenPreview() {
    Text(text = "Exercises Screen Preview")
}
