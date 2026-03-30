package com.alilopez.kt_demohilt.features.recipies.presentation.screens

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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alilopez.kt_demohilt.R
import com.alilopez.kt_demohilt.core.components.SearchBar
import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe
import com.alilopez.kt_demohilt.features.recipies.presentation.components.RecipeCard
import com.alilopez.kt_demohilt.features.recipies.presentation.viewmodels.RecipesListViewModel
import kotlinx.coroutines.launch

@Composable
fun RecipesScreen(
    onNavigateToAddRecipe: () -> Unit,
    onNavigateToEditRecipe: (Int) -> Unit,
    onNavigateToRecipeDetail: (Int) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecipesListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var recipeToDelete by remember { mutableStateOf<Recipe?>(null) }

    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val surfaceColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val secondaryTextColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
    val accentColor = Color(0xFF10B981)

    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val query = uiState.searchQuery.trim()
    val filteredLocalRecipes = if (uiState.isSearchActive && query.isNotBlank()) {
        uiState.localRecipes.filter { it.name.contains(query, ignoreCase = true) }
    } else {
        uiState.localRecipes
    }
    val filteredCommunityRecipes = if (uiState.isSearchActive && query.isNotBlank()) {
        uiState.communityRecipes.filter { it.name.contains(query, ignoreCase = true) }
    } else {
        uiState.communityRecipes
    }
    val filteredRemoteRecipes = if (uiState.isSearchActive && query.isNotBlank()) {
        uiState.remoteRecipes.filter { it.name.contains(query, ignoreCase = true) }
    } else {
        uiState.remoteRecipes
    }

    LaunchedEffect(pagerState.currentPage) {
        when (pagerState.currentPage) {
            1 -> if (uiState.communityRecipes.isEmpty()) viewModel.loadCommunityRecipes()
            2 -> if (uiState.remoteRecipes.isEmpty()) viewModel.loadRemoteRecipes()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = backgroundColor,
        contentWindowInsets = WindowInsets.safeDrawing,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddRecipe,
                containerColor = accentColor,
                contentColor = Color.White,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar receta")
            }
        },
        topBar = {
            RecipesTopSection(
                uiState = uiState,
                currentPage = pagerState.currentPage,
                textColor = textColor,
                secondaryTextColor = secondaryTextColor,
                accentColor = accentColor,
                surfaceColor = surfaceColor,
                isDarkTheme = isDarkTheme,
                onOpenDrawer = onOpenDrawer,
                onNavigateToAddRecipe = onNavigateToAddRecipe,
                onPageSelected = { page -> scope.launch { pagerState.animateScrollToPage(page) } },
                onQueryChange = viewModel::onSearchQueryChange,
                onSearch = viewModel::searchRecipes,
                onClearSearch = viewModel::clearSearch
            )
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor),
            verticalAlignment = Alignment.Top
        ) { page ->
            when (page) {
                0 -> LocalRecipesList(
                    localRecipes = filteredLocalRecipes,
                    isSearchActive = uiState.isSearchActive,
                    accentColor = accentColor,
                    secondaryTextColor = secondaryTextColor,
                    currentUserId = viewModel.currentUserId,
                    onNavigateToAddRecipe = onNavigateToAddRecipe,
                    onNavigateToRecipeDetail = onNavigateToRecipeDetail,
                    onNavigateToEditRecipe = onNavigateToEditRecipe,
                    onDelete = { recipe ->
                        recipeToDelete = recipe
                        showDeleteDialog = true
                    }
                )

                1 -> CommunityRecipesList(
                    recipes = filteredCommunityRecipes,
                    isLoading = uiState.isLoading,
                    isSearchActive = uiState.isSearchActive,
                    accentColor = accentColor,
                    secondaryTextColor = secondaryTextColor,
                    onNavigateToRecipeDetail = onNavigateToRecipeDetail
                )

                2 -> PremiumRecipesList(
                    recipes = filteredRemoteRecipes,
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                    isSearchActive = uiState.isSearchActive,
                    accentColor = accentColor,
                    secondaryTextColor = secondaryTextColor,
                    onNavigateToRecipeDetail = onNavigateToRecipeDetail
                )
            }
        }

        // Diálogo de confirmación para eliminar
        if (showDeleteDialog && recipeToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    recipeToDelete = null
                },
                title = {
                    Text(
                        text = "Eliminar Receta",
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                },
                text = {
                    Text(
                        text = "¿Estás seguro de que deseas eliminar \"${recipeToDelete?.name}\"? Esta acción no se puede deshacer.",
                        color = secondaryTextColor
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            recipeToDelete?.let { recipe ->
                                viewModel.deleteRecipe(recipe.id)
                            }
                            showDeleteDialog = false
                            recipeToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444)
                        )
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            recipeToDelete = null
                        }
                    ) {
                        Text("Cancelar", color = secondaryTextColor)
                    }
                },
                containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
            )
        }
    }
}

@Composable
private fun RecipesTopSection(
    uiState: RecipesListUIState,
    currentPage: Int,
    textColor: Color,
    secondaryTextColor: Color,
    accentColor: Color,
    surfaceColor: Color,
    isDarkTheme: Boolean,
    onOpenDrawer: () -> Unit,
    onNavigateToAddRecipe: () -> Unit,
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

            IconButton(onClick = onNavigateToAddRecipe) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear receta",
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
            placeholder = "Buscar recetas...",
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
            color = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .padding(2.dp)
            ) {
                CompactTab(
                    title = "Mis recetas",
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
private fun LocalRecipesList(
    localRecipes: List<Recipe>,
    isSearchActive: Boolean,
    accentColor: Color,
    secondaryTextColor: Color,
    currentUserId: Int?,
    onNavigateToAddRecipe: () -> Unit,
    onNavigateToRecipeDetail: (Int) -> Unit,
    onNavigateToEditRecipe: (Int) -> Unit,
    onDelete: (Recipe) -> Unit
) {
    if (localRecipes.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = if (isSearchActive) {
                        "No se encontraron recetas"
                    } else {
                        "Aun no has creado recetas"
                    },
                    color = if (isSearchActive) accentColor else secondaryTextColor,
                    fontWeight = FontWeight.SemiBold
                )

                if (!isSearchActive) {
                    Text(
                        text = "Empieza creando tu primera receta personalizada.",
                        color = secondaryTextColor
                    )
                    Button(
                        onClick = onNavigateToAddRecipe,
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Text(text = "Crear receta", color = Color.White)
                    }
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(localRecipes) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    currentUserId = currentUserId,
                    onClick = { onNavigateToRecipeDetail(recipe.id) },
                    onEdit = { onNavigateToEditRecipe(recipe.id) },
                    onDelete = { onDelete(recipe) }
                )
            }
        }
    }
}

@Composable
private fun CommunityRecipesList(
    recipes: List<Recipe>,
    isLoading: Boolean,
    isSearchActive: Boolean,
    accentColor: Color,
    secondaryTextColor: Color,
    onNavigateToRecipeDetail: (Int) -> Unit
) {
    when {
        isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accentColor)
            }
        }

        recipes.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (isSearchActive) {
                        "No se encontraron recetas"
                    } else {
                        "No hay recetas de comunidad disponibles"
                    },
                    color = if (isSearchActive) accentColor else secondaryTextColor
                )
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(recipes) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        currentUserId = null,
                        onClick = { onNavigateToRecipeDetail(recipe.id) },
                        onEdit = {},
                        onDelete = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumRecipesList(
    recipes: List<Recipe>,
    isLoading: Boolean,
    errorMessage: String?,
    isSearchActive: Boolean,
    accentColor: Color,
    secondaryTextColor: Color,
    onNavigateToRecipeDetail: (Int) -> Unit
) {
    when {
        isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accentColor)
            }
        }

        !errorMessage.isNullOrBlank() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Error cargando recetas remotas: $errorMessage",
                    color = Color(0xFFEF4444),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        recipes.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (isSearchActive) {
                        "No se encontraron recetas"
                    } else {
                        "No hay recetas remotas disponibles"
                    },
                    color = if (isSearchActive) accentColor else secondaryTextColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(recipes) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        currentUserId = null,
                        onClick = { onNavigateToRecipeDetail(recipe.id) },
                        onEdit = {},
                        onDelete = {}
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RecipesScreenPreview() {
    Text(text = "Recipes Screen Preview")
}
