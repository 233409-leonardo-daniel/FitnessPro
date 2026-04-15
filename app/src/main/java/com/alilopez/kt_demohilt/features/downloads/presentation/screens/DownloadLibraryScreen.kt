package com.alilopez.kt_demohilt.features.downloads.presentation.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alilopez.kt_demohilt.features.downloads.presentation.viewmodels.DownloadLibraryViewModel
import com.alilopez.kt_demohilt.features.recipeplans.presentation.screens.RecipePlanItem
import com.alilopez.kt_demohilt.features.workoutplans.presentation.screens.WorkoutPlanItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadLibraryScreen(
    onNavigateToWorkoutDetail: (Int, String) -> Unit,
    onNavigateToRecipeDetail: (Int, String) -> Unit,
    onOpenDrawer: () -> Unit,
    viewModel: DownloadLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val tabs = listOf("Rutinas", "Menús")
    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val accentColor = Color(0xFF3B82F6)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Mi Biblioteca Offline", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = null, tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = backgroundColor,
                contentColor = accentColor,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accentColor)
                }
            } else {
                val isEmpty = if (selectedTab == 0) uiState.downloadedWorkouts.isEmpty() 
                             else uiState.downloadedRecipes.isEmpty()

                if (isEmpty) {
                    EmptyLibraryView(textColor)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (selectedTab == 0) {
                            items(uiState.downloadedWorkouts) { plan ->
                                WorkoutPlanItem(
                                    plan = plan,
                                    onClick = { onNavigateToWorkoutDetail(plan.id, plan.name) },
                                    onDelete = { /* Lógica para borrar descarga */ },
                                    onDownload = {}, // Ya está descargado
                                    isDarkTheme = isDarkTheme
                                )
                            }
                        } else {
                            items(uiState.downloadedRecipes) { plan ->
                                RecipePlanItem(
                                    plan = plan,
                                    onClick = { onNavigateToRecipeDetail(plan.id, plan.name) },
                                    onDelete = { /* Lógica para borrar descarga */ },
                                    onDownload = {},
                                    isDarkTheme = isDarkTheme
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
fun EmptyLibraryView(textColor: Color) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.DownloadForOffline,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(16.dp))
        Text("No tienes contenido descargado", color = textColor, fontSize = 18.sp)
        Text("Descarga tus planes para verlos aquí sin internet", color = Color.Gray, fontSize = 14.sp)
    }
}
