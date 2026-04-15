package com.alilopez.kt_demohilt.features.exercise.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alilopez.kt_demohilt.core.components.PremiumGateContent
import com.alilopez.kt_demohilt.core.components.SearchBar
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.presentation.components.ExerciseCard
import com.alilopez.kt_demohilt.features.exercise.presentation.viewmodels.ExerciseViewModel

@Composable
fun ExercisesScreen(
    onNavigateToAddExercise: () -> Unit,
    onOpenDrawer: () -> Unit,
    onNavigateToExerciseDetail: (Int) -> Unit = {},
    onNavigateToEditExercise: (Int) -> Unit = {},
    membership: String? = null,
    onNavigateToPremium: () -> Unit = {},
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUserId = viewModel.currentUserId

    val isPremium = membership?.equals("premium", ignoreCase = true) == true
    val accentColor = Color(0xFF10B981)
    val textColor = MaterialTheme.colorScheme.onBackground
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    var selectedTab by remember { mutableIntStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var exerciseToDelete by remember { mutableStateOf<Exercise?>(null) }

    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            1 -> if (uiState.communityExercises.isEmpty()) viewModel.loadCommunityExercises()
            2 -> if (isPremium && uiState.exercises.isEmpty()) viewModel.loadRemoteExercises()
        }
    }

    val query = uiState.searchQuery.trim()
    val filteredLocal = uiState.localExercises.filterByQuery(uiState.isSearchActive, query)
    val filteredCommunity = uiState.communityExercises.filterByQuery(uiState.isSearchActive, query)
    val filteredRemote = uiState.exercises.filterByQuery(uiState.isSearchActive, query)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddExercise, containerColor = accentColor) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar", tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onOpenDrawer) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                }
                Text(text = "Ejercicios", style = MaterialTheme.typography.titleLarge, color = textColor)
                Spacer(modifier = Modifier.height(1.dp))
            }

            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                onSearch = viewModel::searchExercises,
                onClear = viewModel::clearSearch,
                placeholder = "Buscar ejercicios",
                isDarkTheme = isDark,
                accentColor = accentColor,
                textColor = textColor,
                secondaryTextColor = secondaryTextColor,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TabButton("Mis ejercicios", selectedTab == 0) { selectedTab = 0 }
                TabButton("Comunidad", selectedTab == 1) { selectedTab = 1 }
                TabButton("Premium", selectedTab == 2) { selectedTab = 2 }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedTab) {
                0 -> ExerciseList(
                    exercises = filteredLocal,
                    isLoading = uiState.isLoading,
                    currentUserId = currentUserId,
                    isPremium = isPremium,
                    onNavigateToPremium = onNavigateToPremium,
                    onDownload = viewModel::downloadExercise,
                    onDetail = onNavigateToExerciseDetail,
                    onEdit = onNavigateToEditExercise,
                    onDelete = { exercise ->
                        exerciseToDelete = exercise
                        showDeleteDialog = true
                    }
                )

                1 -> ExerciseList(
                    exercises = filteredCommunity,
                    isLoading = uiState.isLoading,
                    currentUserId = currentUserId,
                    isPremium = isPremium,
                    onNavigateToPremium = onNavigateToPremium,
                    onDownload = viewModel::downloadExercise,
                    onDetail = onNavigateToExerciseDetail,
                    onEdit = {},
                    onDelete = null
                )

                else -> {
                    if (!isPremium) {
                        PremiumGateContent(onNavigateToPremium = onNavigateToPremium, modifier = Modifier.fillMaxSize())
                    } else {
                        Column(modifier = Modifier.fillMaxSize()) {
                            ExerciseList(
                                exercises = filteredRemote,
                                isLoading = uiState.isLoading,
                                currentUserId = currentUserId,
                                isPremium = isPremium,
                                onNavigateToPremium = onNavigateToPremium,
                                onDownload = viewModel::downloadExercise,
                                onDetail = onNavigateToExerciseDetail,
                                onEdit = {},
                                onDelete = null,
                                modifier = Modifier.weight(1f)
                            )

                            if (!uiState.isSearchActive && uiState.hasNextPage) {
                                Button(
                                    onClick = viewModel::loadMoreRemoteExercises,
                                    enabled = !uiState.isLoadingMore && !uiState.isLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                                ) {
                                    if (uiState.isLoadingMore) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.padding(end = 8.dp),
                                            color = Color.White
                                        )
                                    }
                                    Text("Cargar mas", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar ejercicio") },
            text = { Text("Esta accion no se puede deshacer") },
            confirmButton = {
                Button(
                    onClick = {
                        exerciseToDelete?.id?.let(viewModel::deleteExercise)
                        showDeleteDialog = false
                        exerciseToDelete = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ExerciseList(
    exercises: List<Exercise>,
    isLoading: Boolean,
    currentUserId: Int?,
    isPremium: Boolean,
    onNavigateToPremium: () -> Unit,
    onDownload: (Int, String) -> Unit,
    onDetail: (Int) -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: ((Exercise) -> Unit)?,
    modifier: Modifier = Modifier
) {
    if (isLoading && exercises.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (exercises.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay ejercicios para mostrar")
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(exercises) { exercise ->
            ExerciseCard(
                name = exercise.name,
                imageUrl = exercise.gifUrl,
                instructions = exercise.instructions,
                isLocal = exercise.userId != null,
                exerciseType = exercise.exerciseType,
                difficulty = exercise.difficulty,
                currentUserId = currentUserId,
                exerciseUserId = exercise.userId,
                offlineAvailable = exercise.offlineAvailable,
                onDownload = {
                    if (isPremium) {
                        val resolvedId = exercise.id ?: exercise.exerciseId?.toIntOrNull()
                        resolvedId?.let { onDownload(it, exercise.name) }
                    } else {
                        onNavigateToPremium()
                    }
                },
                onClick = { exercise.id?.let(onDetail) },
                onEdit = { exercise.id?.let(onEdit) },
                onDelete = onDelete?.let { { it(exercise) } }
            )
        }
    }
}

@Composable
private fun TabButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF10B981) else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Text(text)
    }
}

private fun List<Exercise>.filterByQuery(isSearchActive: Boolean, query: String): List<Exercise> {
    if (!isSearchActive || query.isBlank()) return this
    return filter { it.name.contains(query, ignoreCase = true) }
}
