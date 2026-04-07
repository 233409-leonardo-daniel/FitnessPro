package com.alilopez.kt_demohilt.features.exercise.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.alilopez.kt_demohilt.features.exercise.presentation.components.gifImageLoader
import com.alilopez.kt_demohilt.features.exercise.presentation.viewmodels.ExerciseDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exerciseId: Int,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExerciseDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()

    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardBackgroundColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val secondaryTextColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
    val accentColor = Color(0xFF10B981)

    LaunchedEffect(Unit) {
        viewModel.loadExercise(exerciseId)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalles del Ejercicio",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
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
                            color = secondaryTextColor
                        )
                    }
                }

                uiState.exercise != null -> {
                    val exercise = uiState.exercise!!
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // IMAGEN / GIF
                        item {
                            if (exercise.gifUrl.isNotBlank()) {
                                AsyncImage(
                                    model = exercise.gifUrl,
                                    imageLoader = gifImageLoader(LocalContext.current),
                                    contentDescription = exercise.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(260.dp)
                                        .clip(RoundedCornerShape(16.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(260.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = "Sin imagen",
                                        tint = secondaryTextColor,
                                        modifier = Modifier.size(80.dp)
                                    )
                                }
                            }
                        }

                        // TÍTULO Y BADGES
                        item {
                            Column {
                                Text(
                                    text = exercise.name,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )

                                if (!exercise.exerciseType.isNullOrBlank() || !exercise.difficulty.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        if (!exercise.exerciseType.isNullOrBlank()) {
                                            Surface(
                                                color = accentColor.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = exercise.exerciseType,
                                                    color = accentColor,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                                )
                                            }
                                        }
                                        if (!exercise.difficulty.isNullOrBlank()) {
                                            Surface(
                                                color = accentColor.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = exercise.difficulty,
                                                    color = accentColor,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // DESCRIPCIÓN
                        if (!exercise.description.isNullOrBlank()) {
                            item {
                                Text(
                                    text = exercise.description,
                                    fontSize = 16.sp,
                                    color = secondaryTextColor,
                                    lineHeight = 22.sp
                                )
                            }
                        }

                        // INSTRUCCIONES
                        if (exercise.instructions.isNotEmpty()) {
                            item {
                                Surface(
                                    color = cardBackgroundColor,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "Instrucciones",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = textColor
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        exercise.instructions.forEachIndexed { index, instruction ->
                                            Row(
                                                verticalAlignment = Alignment.Top,
                                                modifier = Modifier.padding(bottom = 10.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(22.dp)
                                                        .background(accentColor, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "${index + 1}",
                                                        color = Color.White,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = instruction,
                                                    fontSize = 15.sp,
                                                    color = secondaryTextColor,
                                                    lineHeight = 22.sp,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // MÚSCULOS OBJETIVO
                        if (exercise.targetMuscles.isNotEmpty()) {
                            item {
                                Surface(
                                    color = cardBackgroundColor,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "Músculos Objetivo",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = textColor
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        exercise.targetMuscles.forEach { muscle ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(bottom = 6.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(accentColor)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = muscle,
                                                    fontSize = 15.sp,
                                                    color = secondaryTextColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // PARTES DEL CUERPO
                        if (exercise.bodyparts.isNotEmpty()) {
                            item {
                                Surface(
                                    color = cardBackgroundColor,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "Partes del Cuerpo",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = textColor
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            exercise.bodyparts.forEach { part ->
                                                Surface(
                                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(
                                                        text = part,
                                                        fontSize = 13.sp,
                                                        color = textColor,
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }

                else -> {
                    Text(
                        text = "No se encontró el ejercicio",
                        modifier = Modifier.align(Alignment.Center),
                        color = secondaryTextColor
                    )
                }
            }
        }
    }
}
