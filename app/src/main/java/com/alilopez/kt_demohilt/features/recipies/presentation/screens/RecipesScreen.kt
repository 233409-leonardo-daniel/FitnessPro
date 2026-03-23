package com.alilopez.kt_demohilt.features.recipies.presentation.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalFocusManager
import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe
import com.alilopez.kt_demohilt.features.recipies.presentation.components.RecipeCard
import com.alilopez.kt_demohilt.features.recipies.presentation.viewmodels.RecipesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
    val focusManager = LocalFocusManager.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var recipeToDelete by remember { mutableStateOf<Recipe?>(null) }
    var isSearchFocused by remember { mutableStateOf(false) }

    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val secondaryTextColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
    val accentColor = Color(0xFF10B981)

    LaunchedEffect(Unit) {
        viewModel.getRecipies()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Recetas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = textColor
                    )
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
                    IconButton(onClick = {
                        if (uiState.searchQuery.isBlank()) viewModel.getRecipies() else viewModel.searchRecipes()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recargar",
                            tint = accentColor
                        )
                    }
                    IconButton(onClick = onNavigateToAddRecipe) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Recipe",
                            tint = accentColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isSearchFocused = it.isFocused },
                placeholder = { Text("Buscar receta por nombre") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                leadingIcon = {
                    IconButton(
                        onClick = {
                            if (isSearchFocused) {
                                focusManager.clearFocus()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isSearchFocused) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Search,
                            contentDescription = if (isSearchFocused) "Dejar de escribir" else "Buscar",
                            tint = secondaryTextColor
                        )
                    }
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.clearSearch() }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Limpiar búsqueda",
                                    tint = secondaryTextColor
                                )
                            }

                            IconButton(onClick = {
                                viewModel.searchRecipes()
                                focusManager.clearFocus()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Buscar",
                                    tint = accentColor
                                )
                            }
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = accentColor.copy(alpha = if (isDarkTheme) 0.16f else 0.10f),
                    unfocusedContainerColor = accentColor.copy(alpha = if (isDarkTheme) 0.10f else 0.06f),
                    focusedBorderColor = accentColor.copy(alpha = 0.9f),
                    unfocusedBorderColor = accentColor.copy(alpha = 0.35f),
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedLeadingIconColor = accentColor,
                    unfocusedLeadingIconColor = secondaryTextColor,
                    cursorColor = accentColor
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.searchRecipes()
                    focusManager.clearFocus()
                })
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = accentColor
                        )
                    }

                    uiState.errorMessage != null -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = secondaryTextColor,
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                text = "Error al cargar recetas",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColor,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = uiState.errorMessage ?: "",
                                fontSize = 14.sp,
                                color = secondaryTextColor,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = {
                                    if (uiState.searchQuery.isBlank()) viewModel.getRecipies() else viewModel.searchRecipes()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accentColor
                                )
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }

                    uiState.recipies.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = if (uiState.isSearchActive) Icons.Default.Search else Icons.Default.Add,
                                contentDescription = null,
                                tint = secondaryTextColor,
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                text = if (uiState.isSearchActive) "No se encontraron recetas" else "No hay recetas aún",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColor,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = if (uiState.isSearchActive) {
                                    "Intenta con otro nombre"
                                } else {
                                    "Agrega tu primera receta para comenzar"
                                },
                                fontSize = 14.sp,
                                color = secondaryTextColor,
                                textAlign = TextAlign.Center
                            )
                            if (!uiState.isSearchActive) {
                                Button(
                                    onClick = onNavigateToAddRecipe,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = accentColor
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Agregar Receta")
                                }
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(uiState.recipies) { recipe ->
                                RecipeCard(
                                    recipe = recipe,
                                    currentUserId = viewModel.currentUserId,
                                    onClick = {
                                        onNavigateToRecipeDetail(recipe.id)
                                    },
                                    onEdit = {
                                        onNavigateToEditRecipe(recipe.id)
                                    },
                                    onDelete = {
                                        recipeToDelete = recipe
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
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
