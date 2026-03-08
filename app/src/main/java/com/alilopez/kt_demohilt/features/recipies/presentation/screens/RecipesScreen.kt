package com.alilopez.kt_demohilt.features.recipies.presentation.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe
import com.alilopez.kt_demohilt.features.recipies.presentation.components.RecipeCard
import com.alilopez.kt_demohilt.features.recipies.presentation.viewmodels.RecipiesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    onNavigateToAddRecipe: () -> Unit,
    onNavigateToEditRecipe: (Int) -> Unit,
    onNavigateToExercises: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecipiesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var recipeToDelete by remember { mutableStateOf<Recipe?>(null) }

    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val secondaryTextColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)

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
                actions = {
                    IconButton(onClick = onNavigateToExercises) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = "Go to Exercises",
                            tint = Color(0xFF10B981)
                        )
                    }
                    IconButton(onClick = onNavigateToAddRecipe) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Recipe",
                            tint = Color(0xFF10B981)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF10B981)
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
                            onClick = { viewModel.getRecipies() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981)
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
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = secondaryTextColor,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "No hay recetas aún",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Agrega tu primera receta para comenzar",
                            fontSize = 14.sp,
                            color = secondaryTextColor,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = onNavigateToAddRecipe,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981)
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

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.recipies) { recipe ->
                            RecipeCard(
                                recipe = recipe,
                                onClick = {
                                    // TODO: Navegar a detalles de receta
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
