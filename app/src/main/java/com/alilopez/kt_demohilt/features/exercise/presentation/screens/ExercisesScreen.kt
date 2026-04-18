package com.alilopez.kt_demohilt.features.exercise.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alilopez.kt_demohilt.R
import com.alilopez.kt_demohilt.core.components.PremiumGateContent
import com.alilopez.kt_demohilt.core.components.SearchBar
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.presentation.components.ExerciseCard
import com.alilopez.kt_demohilt.features.exercise.presentation.viewmodels.ExerciseViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
    val isDarkTheme = isSystemInDarkTheme()
    val currentUserId = viewModel.currentUserId

    val isPremium = membership?.equals("premium", ignoreCase = true) == true
    val accentColor = Color(0xFF10B981)
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    var showDeleteDialog by remember { mutableStateOf(false) }
    var exerciseToDelete by remember { mutableStateOf<Exercise?>(null) }

    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    val query = uiState.searchQuery.trim()
    val filteredLocal = uiState.localExercises.filterByQuery(uiState.isSearchActive, query)
    val filteredCommunity = uiState.communityExercises.filterByQuery(uiState.isSearchActive, query)
    val filteredRemote = uiState.exercises.filterByQuery(uiState.isSearchActive, query)

    LaunchedEffect(pagerState.currentPage) {
        when (pagerState.currentPage) {
            1 -> if (uiState.communityExercises.isEmpty()) viewModel.loadCommunityExercises()
            2 -> if (isPremium && uiState.exercises.isEmpty()) viewModel.loadRemoteExercises()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadUserExercises()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        contentWindowInsets = WindowInsets.safeDrawing,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddExercise,
                containerColor = accentColor,
                contentColor = Color.White,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar ejercicio")
            }
        },
        topBar = {
            ExercisesTopSection(
                uiState = uiState,
                currentPage = pagerState.currentPage,
                textColor = textColor,
                secondaryTextColor = secondaryTextColor,
                accentColor = accentColor,
                surfaceColor = backgroundColor,
                isDarkTheme = isDarkTheme,
                onOpenDrawer = onOpenDrawer,
                onNavigateToAddExercise = onNavigateToAddExercise,
                onPageSelected = { page -> scope.launch { pagerState.animateScrollToPage(page) } },
                onQueryChange = viewModel::onSearchQueryChange,
                onSearch = viewModel::searchExercises,
                onClearSearch = viewModel::clearSearch
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = {
                when (pagerState.currentPage) {
                    0 -> viewModel.loadUserExercises()
                    1 -> viewModel.loadCommunityExercises()
                    else -> viewModel.loadRemoteExercises()
                }
            },
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
                    0 -> ExerciseList(
                        exercises = filteredLocal,
                        isLoading = uiState.isLoading,
                        currentUserId = currentUserId,
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
                        onDetail = onNavigateToExerciseDetail,
                        onEdit = {},
                        onDelete = null
                    )

                    2 -> if (!isPremium) {
                        PremiumGateContent(
                            onNavigateToPremium = onNavigateToPremium,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(modifier = Modifier.fillMaxSize()) {
                            ExerciseList(
                                exercises = filteredRemote,
                                isLoading = uiState.isLoading,
                                currentUserId = currentUserId,
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
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                                ) {
                                    if (uiState.isLoadingMore) {
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .size(18.dp)
                                                .padding(end = 8.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    }
                                    Text("Cargar más", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog && exerciseToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                exerciseToDelete = null
            },
            title = {
                Text(
                    text = "Eliminar ejercicio",
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas eliminar \"${exerciseToDelete?.name}\"? Esta acción no se puede deshacer.",
                    color = secondaryTextColor
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        exerciseToDelete?.id?.let(viewModel::deleteExercise)
                        showDeleteDialog = false
                        exerciseToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    exerciseToDelete = null
                }) {
                    Text("Cancelar", color = secondaryTextColor)
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    }
}

@Composable
private fun ExercisesTopSection(
    uiState: ExercisesUiState,
    currentPage: Int,
    textColor: Color,
    secondaryTextColor: Color,
    accentColor: Color,
    surfaceColor: Color,
    isDarkTheme: Boolean,
    onOpenDrawer: () -> Unit,
    onNavigateToAddExercise: () -> Unit,
    onPageSelected: (Int) -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClearSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(surfaceColor)
            .statusBarsPadding()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Abrir menu",
                    tint = textColor
                )
            }

            Image(
                painter = painterResource(id = R.drawable.fitness_pro_icon_round),
                contentDescription = "Logo FitnessPro",
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "FitnessPro",
                color = textColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onNavigateToAddExercise) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear ejercicio",
                    tint = accentColor
                )
            }
        }

        SearchBar(
            query = uiState.searchQuery,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            onClear = onClearSearch,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = "Buscar ejercicios...",
            isDarkTheme = isDarkTheme,
            accentColor = accentColor,
            textColor = textColor,
            secondaryTextColor = secondaryTextColor
        )

        Surface(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth()
                .height(34.dp),
            shape = RoundedCornerShape(11.dp),
            color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .padding(2.dp)
            ) {
                ExerciseCompactTab(
                    title = "Mis ejercicios",
                    selected = currentPage == 0,
                    accentColor = accentColor,
                    textColor = textColor,
                    modifier = Modifier.weight(1f),
                    onClick = { onPageSelected(0) }
                )
                ExerciseCompactTab(
                    title = "Comunidad",
                    selected = currentPage == 1,
                    accentColor = accentColor,
                    textColor = textColor,
                    modifier = Modifier.weight(1f),
                    onClick = { onPageSelected(1) }
                )
                ExerciseCompactTab(
                    title = "Premium",
                    selected = currentPage == 2,
                    accentColor = accentColor,
                    textColor = textColor,
                    modifier = Modifier.weight(1f),
                    onClick = { onPageSelected(2) }
                )
            }
        }
    }
}

@Composable
private fun ExerciseCompactTab(
    title: String,
    selected: Boolean,
    accentColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(9.dp))
            .background(if (selected) accentColor else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (selected) Color.White else textColor.copy(alpha = 0.84f),
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun ExerciseList(
    exercises: List<Exercise>,
    isLoading: Boolean,
    currentUserId: Int?,
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
        contentPadding = PaddingValues(16.dp),
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
                onClick = { exercise.id?.let(onDetail) },
                onEdit = { exercise.id?.let(onEdit) },
                onDelete = onDelete?.let { { it(exercise) } }
            )
        }
    }
}

private fun List<Exercise>.filterByQuery(isSearchActive: Boolean, query: String): List<Exercise> {
    if (!isSearchActive || query.isBlank()) return this
    return filter { it.name.contains(query, ignoreCase = true) }
}
