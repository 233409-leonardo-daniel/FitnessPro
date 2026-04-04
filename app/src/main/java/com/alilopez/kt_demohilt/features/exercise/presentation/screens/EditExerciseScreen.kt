package com.alilopez.kt_demohilt.features.exercise.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alilopez.kt_demohilt.core.components.InputFitness
import com.alilopez.kt_demohilt.features.exercise.presentation.viewmodels.EditExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExerciseScreen(
    exerciseId: Int,
    onNavigateBack: () -> Unit,
    viewModel: EditExerciseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()

    val name by viewModel.name.collectAsStateWithLifecycle()
    val description by viewModel.description.collectAsStateWithLifecycle()
    val instructions by viewModel.instructions.collectAsStateWithLifecycle()
    val selectedExerciseType by viewModel.selectedExerciseType.collectAsStateWithLifecycle()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    val selectedDays by viewModel.selectedDays.collectAsStateWithLifecycle()

    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val labelColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
    val textFieldBackground = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val textFieldBorder = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)
    val placeholderColor = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8)

    val exerciseTypes = listOf("CARDIO", "FUERZA", "ESTIRAMIENTO", "AEROBICOS", "YOGA", "LEVANTAMIENTO DE PESAS", "PLIOMETRIA")
    val difficultyOptions = listOf("Facil", "Medio", "Dificil")
    val daysOfWeek = listOf("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo")

    LaunchedEffect(exerciseId) {
        viewModel.loadExercise(exerciseId)
    }

    LaunchedEffect(uiState.exerciseUpdated) {
        if (uiState.exerciseUpdated) {
            viewModel.resetExerciseUpdated()
            onNavigateBack()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Editar Ejercicio",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = labelColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading && uiState.exercise == null -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF10B981)
                    )
                }

                uiState.exercise == null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Ejercicio no encontrado",
                            fontWeight = FontWeight.SemiBold,
                            color = textColor
                        )
                        Button(
                            onClick = onNavigateBack,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Text("Volver", color = Color.White)
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                            .padding(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "NOMBRE DEL EJERCICIO",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = labelColor,
                                letterSpacing = 1.2.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            InputFitness(
                                value = name,
                                onValueChange = viewModel::onNameChange,
                                placeholder = "e.g. Sentadillas con barra"
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "TIPO DE EJERCICIO (OPCIONAL)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = labelColor,
                                letterSpacing = 1.2.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            exerciseTypes.forEach { option ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.onExerciseTypeChange(
                                                if (selectedExerciseType == option) null else option
                                            )
                                        }
                                ) {
                                    RadioButton(
                                        selected = selectedExerciseType == option,
                                        onClick = {
                                            viewModel.onExerciseTypeChange(
                                                if (selectedExerciseType == option) null else option
                                            )
                                        },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = Color(0xFF10B981),
                                            unselectedColor = labelColor
                                        )
                                    )
                                    Text(text = option, fontSize = 14.sp, color = textColor)
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "DIFICULTAD",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = labelColor,
                                letterSpacing = 1.2.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            difficultyOptions.forEach { option ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.onDifficultyChange(option) }
                                ) {
                                    RadioButton(
                                        selected = selectedDifficulty == option,
                                        onClick = { viewModel.onDifficultyChange(option) },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = Color(0xFF10B981),
                                            unselectedColor = labelColor
                                        )
                                    )
                                    Text(text = option, fontSize = 14.sp, color = textColor)
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "DIAS PROGRAMADOS (OPCIONAL)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = labelColor,
                                letterSpacing = 1.2.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            daysOfWeek.forEach { day ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Checkbox(
                                        checked = selectedDays.contains(day),
                                        onCheckedChange = { viewModel.onDayToggle(day) },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Color(0xFF10B981),
                                            uncheckedColor = labelColor
                                        )
                                    )
                                    Text(text = day, fontSize = 14.sp, color = textColor)
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "DESCRIPCION",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = labelColor,
                                letterSpacing = 1.2.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            OutlinedTextField(
                                value = description,
                                onValueChange = viewModel::onDescriptionChange,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                placeholder = {
                                    Text(
                                        text = "Describe el ejercicio...",
                                        color = placeholderColor,
                                        fontSize = 16.sp
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = textFieldBackground,
                                    unfocusedContainerColor = textFieldBackground,
                                    focusedBorderColor = Color(0xFF10B981),
                                    unfocusedBorderColor = textFieldBorder,
                                    focusedTextColor = textColor,
                                    unfocusedTextColor = textColor,
                                    cursorColor = Color(0xFF10B981)
                                )
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "INSTRUCCIONES (OPCIONAL)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = labelColor,
                                letterSpacing = 1.2.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            OutlinedTextField(
                                value = instructions,
                                onValueChange = viewModel::onInstructionsChange,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                placeholder = {
                                    Text(
                                        text = "1. Posicion inicial...\n2. Ejecutar movimiento...\n3. Volver a posicion...",
                                        color = placeholderColor,
                                        fontSize = 16.sp
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = textFieldBackground,
                                    unfocusedContainerColor = textFieldBackground,
                                    focusedBorderColor = Color(0xFF10B981),
                                    unfocusedBorderColor = textFieldBorder,
                                    focusedTextColor = textColor,
                                    unfocusedTextColor = textColor,
                                    cursorColor = Color(0xFF10B981)
                                )
                            )
                        }

                        if (uiState.errorMessage != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF991B1B).copy(alpha = 0.1f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFEF4444)
                                    )
                                    Text(
                                        text = uiState.errorMessage.orEmpty(),
                                        color = Color(0xFFEF4444),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = { viewModel.updateExercise(exerciseId) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = !uiState.isLoading && name.isNotBlank() && description.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981),
                                disabledContainerColor = Color(0xFF10B981).copy(alpha = 0.5f)
                            )
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.height(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Text(
                                        text = "Actualizar Ejercicio",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

