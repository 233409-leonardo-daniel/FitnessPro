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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alilopez.kt_demohilt.R
import com.alilopez.kt_demohilt.core.components.PremiumGateContent
import com.alilopez.kt_demohilt.core.components.SearchBar
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.presentation.components.ExerciseCard
import com.alilopez.kt_demohilt.features.exercise.presentation.viewmodels.ExerciseViewModel
import kotlinx.coroutines.launch

private val bodyParts = listOf(
    "ESPALDA",
    "PECHO",
    "ANTEBRAZOS",
    "CUELLO",
    "HOMBROS",
    "MUSLOS",
    "CINTURA",
    "BRAZOS",
    "PANTORRILLAS"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(
    onNavigateToAddExercise: () -> Unit,
    onOpenDrawer: () -> Unit,
    membership: String? = null,
    onNavigateToPremium: () -> Unit = {},
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()

    // Usar colores del tema unificado
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surfaceContainer
    val textColor = MaterialTheme.colorScheme.onBackground
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val accentColor = Color(0xFF10B981)
    val clearButtonColor = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)

    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val query = uiState.searchQuery.trim()

    val filteredLocalExercises = if (uiState.isSearchActive && query.isNotBlank()) {
        uiState.localExercises.filter { it.name.contains(query, ignoreCase = true) }
    } else {
        uiState.localExercises
    }

    val filteredExploreExercises = if (uiState.isSearchActive && query.isNotBlank()) {
        uiState.exercises.filter { it.name.contains(query, ignoreCase = true) }
    } else {
        uiState.exercises
    }

    val query = uiState.searchQuery.trim()
    val filteredLocalExercises = if (uiState.isSearchActive && query.isNotBlank()) {
        uiState.localExercises.filter { it.name.contains(query, ignoreCase = true) }
    } else {
        uiState.localExercises
    }
    val filteredCommunityExercises = if (uiState.isSearchActive && query.isNotBlank()) {
        uiState.communityExercises.filter { it.name.contains(query, ignoreCase = true) }
    } else {
        uiState.communityExercises
    }
    val filteredRemoteExercises = if (uiState.isSearchActive && query.isNotBlank()) {
        uiState.exercises.filter { it.name.contains(query, ignoreCase = true) }
    } else {
        uiState.exercises
    }

    LaunchedEffect(pagerState.currentPage) {
        when (pagerState.currentPage) {
            1 -> if (uiState.communityExercises.isEmpty()) viewModel.loadCommunityExercises()
            2 -> if (uiState.exercises.isEmpty()) viewModel.loadRemoteExercises()
        }
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
                surfaceColor = backgroundColor, // Cambiado para que coincida con el fondo
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
            isRefreshing = uiState.isRefreshing,
            onRefresh = {
                when (pagerState.currentPage) {
                    0 -> viewModel.loadUserExercises(isUserRefresh = true)
                    1 -> viewModel.loadCommunityExercises(isUserRefresh = true)
                    else -> viewModel.loadRemoteExercises(isUserRefresh = true)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                onSearch = { viewModel.searchExercises() },
                onClear = { viewModel.clearSearch() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = "Buscar ejercicio por nombre",
                isDarkTheme = isDarkTheme,
                accentColor = accentColor,
                textColor = textColor,
                secondaryTextColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top
            ) { page ->
                when (page) {
                    0 -> LocalExercisesList(
                        localExercises = filteredLocalExercises,
                        isSearchActive = uiState.isSearchActive,
                        accentColor = accentColor,
                        secondaryTextColor = secondaryTextColor,
                        onNavigateToAddExercise = onNavigateToAddExercise
                    )

                    1 -> CommunityExercisesList(
                        exercises = filteredCommunityExercises,
                        isLoading = uiState.isLoading,
                        isSearchActive = uiState.isSearchActive,
                        accentColor = accentColor,
                        secondaryTextColor = secondaryTextColor
                    )

                    2 -> if (membership != null && membership != "gratuito") {
                        RemoteExercisesList(
                            exercises = filteredRemoteExercises,
                            uiState = uiState,
                            viewModel = viewModel,
                            textColor = textColor,
                            accentColor = accentColor,
                            clearButtonColor = clearButtonColor,
                            surfaceColor = surfaceColor
                        )
                    } else {
                        PremiumGateContent(onNavigateToPremium = onNavigateToPremium)
                    }
                }
            }
        }
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
                painter = painterResource(id = R.drawable.logo_sinletras),
                contentDescription = "Logo FitnessPro",
                modifier = Modifier.size(28.dp)
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
                CompactTab(
                    title = "Mis ejercicios",
                    selected = currentPage == 0,
                    accentColor = accentColor,
                    textColor = textColor,
                    modifier = Modifier.weight(1f),
                    onClick = { onPageSelected(0) }
                )
                CompactTab(
                    title = "Comunidad",
                    selected = currentPage == 1,
                    accentColor = accentColor,
                    textColor = textColor,
                    modifier = Modifier.weight(1f),
                    onClick = { onPageSelected(1) }
                )
                CompactTab(
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
private fun CompactTab(
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
private fun LocalExercisesList(
    localExercises: List<Exercise>,
    isSearchActive: Boolean,
    accentColor: Color,
    secondaryTextColor: Color,
    onNavigateToAddExercise: () -> Unit
) {
    if (localExercises.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = if (isSearchActive) {
                        "No se encontraron ejercicios"
                    } else {
                        "Aun no has creado ejercicios"
                    },
                    color = if (isSearchActive) accentColor else secondaryTextColor,
                    fontWeight = FontWeight.SemiBold
                )

                if (!isSearchActive) {
                    Text(
                        text = "Empieza creando tu primer ejercicio personalizado.",
                        color = secondaryTextColor
                    )
                    Button(
                        onClick = onNavigateToAddExercise,
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Text(text = "Crear ejercicio", color = Color.White)
                    }
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
private fun CommunityExercisesList(
    exercises: List<Exercise>,
    isLoading: Boolean,
    isSearchActive: Boolean,
    accentColor: Color,
    secondaryTextColor: Color
) {
    when {
        isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accentColor)
            }
        }

        exercises.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (isSearchActive) {
                        "No se encontraron ejercicios"
                    } else {
                        "No hay ejercicios de comunidad disponibles"
                    },
                    color = if (isSearchActive) accentColor else secondaryTextColor
                )
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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

@Composable
private fun RemoteExercisesList(
    exercises: List<Exercise>,
    uiState: ExercisesUiState,
    viewModel: ExerciseViewModel,
    textColor: Color,
    accentColor: Color,
    clearButtonColor: Color,
    surfaceColor: Color
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { viewModel.toggleFilter() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Filtrar por parte", color = Color.White)
                }

                if (uiState.selectedBodyPart != null) {
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.clearFilters() },
                        colors = ButtonDefaults.buttonColors(containerColor = clearButtonColor)
                    ) {
                        Text("Limpiar", color = textColor)
                    }
                }
            }

            DropdownMenu(
                expanded = uiState.isFilterExpanded,
                onDismissRequest = { viewModel.toggleFilter() },
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                bodyParts.forEach { bodyPart ->
                    val selected = uiState.selectedBodyPart == bodyPart
                    DropdownMenuItem(
                        text = { Text(text = bodyPart, color = textColor) },
                        trailingIcon = {
                            if (selected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = accentColor
                                )
                            }
                        },
                        onClick = {
                            viewModel.onBodyPartChecked(bodyPart, !selected)
                        }
                    )
                }

                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Aplicar filtros",
                            fontWeight = FontWeight.SemiBold,
                            color = accentColor
                        )
                    },
                    onClick = { viewModel.applyFilters() }
                )
            }
        }

        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accentColor)
                }
            }

            exercises.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (uiState.isSearchActive) {
                            "No se encontraron ejercicios"
                        } else {
                            "No hay ejercicios remotos disponibles"
                        },
                        color = if (uiState.isSearchActive) accentColor else textColor.copy(alpha = 0.7f)
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
}

@Preview
@Composable
private fun ExercisesScreenPreview() {
    Text(text = "Exercises Screen Preview")
}
