package com.alilopez.kt_demohilt.features.user.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alilopez.kt_demohilt.core.components.InputFitness
import com.alilopez.kt_demohilt.features.user.presentation.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    isOnboarding: Boolean = false,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()
    
    val name by viewModel.name.collectAsStateWithLifecycle()
    val lastname by viewModel.lastname.collectAsStateWithLifecycle()
    val birthdate by viewModel.birthdate.collectAsStateWithLifecycle()
    val weight by viewModel.weight.collectAsStateWithLifecycle()
    val height by viewModel.height.collectAsStateWithLifecycle()
    val gender by viewModel.gender.collectAsStateWithLifecycle()
    val targetWeight by viewModel.targetWeight.collectAsStateWithLifecycle()

    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val accentColor = Color(0xFF10B981)

    var showGenderMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (isOnboarding) "Completa tu Perfil" else "Editar Perfil",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    if (!isOnboarding) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isOnboarding) {
                    Text(
                        text = "¡Bienvenido! Necesitamos unos datos más para empezar.",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                InputFitness(
                    value = name,
                    onValueChange = { viewModel.onNameChange(it) },
                    label = "Nombre",
                    placeholder = "Tu nombre",
                    leadingIcon = Icons.Default.Person
                )

                InputFitness(
                    value = lastname,
                    onValueChange = { viewModel.onLastnameChange(it) },
                    label = "Apellido",
                    placeholder = "Tu apellido",
                    leadingIcon = Icons.Default.Person
                )

                InputFitness(
                    value = birthdate,
                    onValueChange = { viewModel.onBirthdateChange(it) },
                    label = "Fecha de Nacimiento",
                    placeholder = "YYYY-MM-DD",
                    leadingIcon = Icons.Default.DateRange
                )

                InputFitness(
                    value = weight,
                    onValueChange = { viewModel.onWeightChange(it) },
                    label = "Peso Actual",
                    placeholder = "Ej. 70 (kg)",
                    leadingIcon = Icons.Default.MonitorWeight
                )

                InputFitness(
                    value = height,
                    onValueChange = { viewModel.onHeightChange(it) },
                    label = "Altura",
                    placeholder = "Ej. 175 (cm)",
                    leadingIcon = Icons.Default.Height
                )

                InputFitness(
                    value = targetWeight,
                    onValueChange = { viewModel.onTargetWeightChange(it) },
                    label = "Peso Objetivo",
                    placeholder = "Ej. 65 (kg)",
                    leadingIcon = Icons.Default.Flag
                )

                // Selector de Género
                Box(modifier = Modifier.fillMaxWidth()) {
                    InputFitness(
                        value = gender,
                        onValueChange = {},
                        label = "Género",
                        placeholder = "Selecciona tu género",
                        leadingIcon = Icons.Default.Face,
                        trailingIcon = Icons.Default.ArrowDropDown,
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { showGenderMenu = true })
                    
                    DropdownMenu(
                        expanded = showGenderMenu,
                        onDismissRequest = { showGenderMenu = false },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Hombre") },
                            onClick = { 
                                viewModel.onGenderChange("Hombre")
                                showGenderMenu = false 
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mujer") },
                            onClick = { 
                                viewModel.onGenderChange("Mujer")
                                showGenderMenu = false 
                            }
                        )
                    }
                }

                if (uiState.errorMessage != null) {
                    Text(text = uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.onSaveClick() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
