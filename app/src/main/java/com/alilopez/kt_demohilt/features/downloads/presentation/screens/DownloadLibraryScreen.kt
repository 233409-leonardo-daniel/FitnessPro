package com.alilopez.kt_demohilt.features.downloads.presentation.screens

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
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alilopez.kt_demohilt.R
import com.alilopez.kt_demohilt.features.downloads.presentation.viewmodels.DownloadLibraryViewModel
import com.alilopez.kt_demohilt.features.recipeplans.presentation.screens.RecipePlanItem
import com.alilopez.kt_demohilt.features.workoutplans.presentation.screens.WorkoutPlanItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadLibraryScreen(
    onNavigateToWorkoutDetail: (Int, String) -> Unit,
    onNavigateToRecipeDetail: (Int, String) -> Unit,
    onOpenDrawer: () -> Unit,
    membership: String?,
    viewModel: DownloadLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()
    val isPremium = membership != null && membership != "gratuito"

    val accentColor = Color(0xFF10B981)
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground

    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
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
                }

                Surface(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
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
                        LibraryTab(
                            title = "Rutinas",
                            selected = pagerState.currentPage == 0,
                            accentColor = accentColor,
                            textColor = textColor,
                            modifier = Modifier.weight(1f),
                            onClick = { scope.launch { pagerState.animateScrollToPage(0) } }
                        )
                        LibraryTab(
                            title = "Menús",
                            selected = pagerState.currentPage == 1,
                            accentColor = accentColor,
                            textColor = textColor,
                            modifier = Modifier.weight(1f),
                            onClick = { scope.launch { pagerState.animateScrollToPage(1) } }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.loadDownloadedContent() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top
            ) { page ->
                val isEmpty = if (page == 0) uiState.downloadedWorkouts.isEmpty()
                              else uiState.downloadedRecipes.isEmpty()

                if (uiState.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = accentColor)
                    }
                } else if (isEmpty) {
                    EmptyLibraryView(textColor = textColor, accentColor = accentColor)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (page == 0) {
                            items(uiState.downloadedWorkouts) { plan ->
                                WorkoutPlanItem(
                                    plan = plan,
                                    isPremium = isPremium,
                                    onClick = { onNavigateToWorkoutDetail(plan.id, plan.name) },
                                    onDelete = { viewModel.removeWorkoutDownload(plan.id) },
                                    onDownload = {},
                                    isDarkTheme = isDarkTheme
                                )
                            }
                        } else {
                            items(uiState.downloadedRecipes) { plan ->
                                RecipePlanItem(
                                    plan = plan,
                                    isPremium = isPremium,
                                    onClick = { onNavigateToRecipeDetail(plan.id, plan.name) },
                                    onDelete = { viewModel.removeRecipeDownload(plan.id) },
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
private fun LibraryTab(
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
fun EmptyLibraryView(
    textColor: Color,
    accentColor: Color = Color(0xFF10B981)
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.DownloadForOffline,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = accentColor.copy(alpha = 0.4f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "No tienes contenido descargado",
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Descarga tus planes para verlos aquí sin internet",
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}
