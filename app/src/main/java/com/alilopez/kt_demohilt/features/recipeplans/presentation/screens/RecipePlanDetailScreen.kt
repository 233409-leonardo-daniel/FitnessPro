package com.alilopez.kt_demohilt.features.recipeplans.presentation.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe
import com.alilopez.kt_demohilt.features.recipies.presentation.components.RecipeCard
import com.alilopez.kt_demohilt.features.recipeplans.presentation.viewmodels.RecipePlanDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipePlanDetailScreen(
    planId: Int,
    planName: String,
    onNavigateBack: () -> Unit,
    onNavigateToAddRecipe: () -> Unit,
    viewModel: RecipePlanDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()

    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val accentColor = Color(0xFF10B981)

    LaunchedEffect(planId) {
        viewModel.loadPlanRecipes(planId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text(planName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = textColor)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadAvailableRecipes() }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar existente", tint = accentColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = accentColor)
            } else if (uiState.recipes.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Este menú está vacío", color = textColor, fontSize = 18.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.loadAvailableRecipes() },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Text("Añadir receta existente")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onNavigateToAddRecipe,
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = SolidColor(accentColor))
                    ) {
                        Text("Crear receta nueva", color = accentColor)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.recipes) { recipe ->
                        Box(modifier = Modifier.fillMaxWidth()) {
                            RecipeCard(
                                recipe = recipe,
                                currentUserId = viewModel.currentUserId,
                                onClick = { /* Detalle de receta opcional */ }
                            )
                            IconButton(
                                onClick = { viewModel.removeRecipeFromPlan(planId, recipe.id) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Quitar del menú",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(
                                onClick = { viewModel.loadAvailableRecipes() },
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Añadir receta existente")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = onNavigateToAddRecipe) {
                                Text("¿No encuentras la receta? Créala aquí", color = accentColor)
                            }
                        }
                    }
                }
            }
        }

        if (uiState.isAddingRecipe) {
            AddRecipeModal(
                recipes = uiState.availableRecipes,
                onDismiss = { viewModel.closeAddRecipe() },
                onSelect = { recipeId -> viewModel.addRecipeToPlan(planId, recipeId) },
                isDarkTheme = isDarkTheme,
                accentColor = accentColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeModal(
    recipes: List<Recipe>,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
    isDarkTheme: Boolean,
    accentColor: Color
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                "Selecciona una receta",
                modifier = Modifier.padding(16.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            if (recipes.isEmpty()) {
                Text(
                    "No tienes recetas disponibles para añadir.",
                    modifier = Modifier.padding(24.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxHeight(0.6f)) {
                    items(recipes) { recipe ->
                        ListItem(
                            headlineContent = { Text(recipe.name) },
                            supportingContent = { Text(recipe.mealType ?: "") },
                            trailingContent = {
                                Button(
                                    onClick = { onSelect(recipe.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                                ) {
                                    Text("Añadir")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
