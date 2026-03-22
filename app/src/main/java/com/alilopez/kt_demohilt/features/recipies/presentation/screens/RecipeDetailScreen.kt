package com.alilopez.kt_demohilt.features.recipies.presentation.screens

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alilopez.kt_demohilt.features.recipies.presentation.viewmodels.RecipeDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Int,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()

    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardBackgroundColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val secondaryTextColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
    val accentColor = Color(0xFF10B981)
    val dividerColor = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)

    LaunchedEffect(Unit) {
        viewModel.loadRecipe(recipeId)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalles de Receta",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = textColor
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
                        color = accentColor
                    )
                }

                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error",
                            color = Color.Red,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.errorMessage ?: "Error desconocido",
                            color = secondaryTextColor,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                uiState.recipe != null -> {
                    val recipe = uiState.recipe!!
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // IMAGEN
                        item {
                            Box {
                                if (!recipe.imageUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(recipe.imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = recipe.name,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(250.dp)
                                            .clip(RoundedCornerShape(16.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(250.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Image,
                                            contentDescription = "Sin imagen",
                                            tint = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                                            modifier = Modifier.size(80.dp)
                                        )
                                    }
                                }

                                if (!recipe.mealType.isNullOrBlank()) {
                                    Surface(
                                        color = accentColor,
                                        shape = RoundedCornerShape(50),
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = recipe.mealType,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // TÍTULO Y BOTONES
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = recipe.name,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor,
                                    modifier = Modifier.weight(1f)
                                )

                                // Botón reproducir audio
                                if (!recipe.audioUrl.isNullOrBlank()) {
                                    var isPlaying by remember { mutableStateOf(false) }

                                    IconButton(onClick = {
                                        if (!isPlaying) {
                                            isPlaying = true
                                            val mediaPlayer = MediaPlayer()
                                            try {
                                                mediaPlayer.setDataSource(recipe.audioUrl)
                                                mediaPlayer.setOnPreparedListener { it.start() }
                                                mediaPlayer.setOnCompletionListener {
                                                    isPlaying = false
                                                    it.release()
                                                }
                                                mediaPlayer.setOnErrorListener { mp, _, _ ->
                                                    isPlaying = false
                                                    mp.release()
                                                    true
                                                }
                                                mediaPlayer.prepareAsync()
                                            } catch (_: Exception) {
                                                isPlaying = false
                                                mediaPlayer.release()
                                            }
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Reproducir audio",
                                            tint = if (isPlaying) Color(0xFF3B82F6) else accentColor,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // DESCRIPCIÓN
                        item {
                            Text(
                                text = recipe.description,
                                fontSize = 16.sp,
                                color = secondaryTextColor,
                                lineHeight = 22.sp
                            )
                        }

                        // INGREDIENTES
                        item {
                            Surface(
                                color = cardBackgroundColor,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Ingredientes",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = textColor
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    val ingredientsList = recipe.ingredients.split(",")
                                    ingredientsList.forEach { ingredient ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(RoundedCornerShape(50))
                                                    .background(accentColor)
                                            )

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Text(
                                                text = ingredient.trim(),
                                                fontSize = 15.sp,
                                                color = secondaryTextColor
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // INSTRUCCIONES
                        item {
                            Surface(
                                color = cardBackgroundColor,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Instrucciones",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = textColor
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = recipe.instructions,
                                        fontSize = 15.sp,
                                        color = secondaryTextColor,
                                        lineHeight = 22.sp
                                    )
                                }
                            }
                        }

                        // DÍAS PROGRAMADOS
                        item {
                            if (recipe.scheduledDays.isNotEmpty()) {
                                Surface(
                                    color = cardBackgroundColor,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Días Programados",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = textColor
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = recipe.scheduledDays.joinToString(", "),
                                            fontSize = 15.sp,
                                            color = secondaryTextColor
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                else -> {
                    Text(
                        text = "No se encontró la receta",
                        modifier = Modifier.align(Alignment.Center),
                        color = secondaryTextColor
                    )
                }
            }
        }
    }
}


