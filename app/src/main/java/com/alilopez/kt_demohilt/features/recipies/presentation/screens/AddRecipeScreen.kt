package com.alilopez.kt_demohilt.features.recipies.presentation.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alilopez.kt_demohilt.core.components.InputFitness
import com.alilopez.kt_demohilt.features.recipies.presentation.viewmodels.AddRecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddRecipeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()

    val name by viewModel.name.collectAsStateWithLifecycle()
    val description by viewModel.description.collectAsStateWithLifecycle()
    val ingredients by viewModel.ingredients.collectAsStateWithLifecycle()
    val instructions by viewModel.instructions.collectAsStateWithLifecycle()
    val selectedMealType by viewModel.selectedMealType.collectAsStateWithLifecycle()
    val selectedDays by viewModel.selectedDays.collectAsStateWithLifecycle()
    val photoUri by viewModel.photoUri.collectAsStateWithLifecycle()
    val photoTaken by viewModel.photoTaken.collectAsStateWithLifecycle()

    // Colores uniformes con LoginScreen
    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val labelColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
    val textFieldBackground = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val textFieldBorder = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)
    val placeholderColor = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8)

    val mealTypeOptions = listOf("Desayuno", "Almuerzo", "Cena")
    val daysOfWeek = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    // Launcher para tomar foto con la cámara
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        viewModel.onPhotoTaken(success)
    }

    // Launcher para solicitar permiso de cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val uri = viewModel.createPhotoUri()
            takePictureLauncher.launch(uri)
        }
    }

    // Observar cuando se crea exitosamente la receta
    LaunchedEffect(uiState.recipeCreated) {
        if (uiState.recipeCreated) {
            viewModel.resetRecipeCreated()
            onNavigateBack()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    @Suppress("DEPRECATION")
                    Text(
                        text = "Agregar Receta",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .padding(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Foto de la receta (Cámara)
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "FOTO DE LA RECETA (OPCIONAL)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = labelColor,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp,
                                color = if (photoTaken) Color(0xFF10B981) else textFieldBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoTaken && photoUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(photoUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Foto tomada",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = "Tomar foto",
                                    tint = labelColor,
                                    modifier = Modifier.size(40.dp)
                                )
                                Text(
                                    text = "Toca para tomar una foto",
                                    fontSize = 14.sp,
                                    color = placeholderColor
                                )
                            }
                        }
                    }
                }

                // Recipe Name
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "NOMBRE DE LA RECETA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = labelColor,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    InputFitness(
                        value = name,
                        onValueChange = { viewModel.onNameChange(it) },
                        placeholder = "e.g. Grilled Salmon Salad"
                    )
                }

                // Meal Type - Radio Buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "TIPO DE COMIDA (OPCIONAL)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = labelColor,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    mealTypeOptions.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onMealTypeChange(if (selectedMealType == option) null else option)
                                }
                        ) {
                            RadioButton(
                                selected = selectedMealType == option,
                                onClick = {
                                    viewModel.onMealTypeChange(if (selectedMealType == option) null else option)
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFF10B981),
                                    unselectedColor = labelColor
                                )
                            )
                            Text(
                                text = option,
                                fontSize = 14.sp,
                                color = textColor
                            )
                        }
                    }
                }

                // Scheduled Days
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "DÍAS PROGRAMADOS (OPCIONAL)",
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
                            Text(
                                text = day,
                                fontSize = 14.sp,
                                color = textColor
                            )
                        }
                    }
                }

                // Description
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "DESCRIPCIÓN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = labelColor,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { viewModel.onDescriptionChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = {
                            Text(
                                text = "Escribe una breve descripción del plato...",
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

                // Ingredients
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "INGREDIENTES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = labelColor,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    OutlinedTextField(
                        value = ingredients,
                        onValueChange = { viewModel.onIngredientsChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = {
                            Text(
                                text = "Lista los ingredientes...\nej. 1 taza de quinoa, 200g de tomates cherry",
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

                // Instructions
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "INSTRUCCIONES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = labelColor,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    OutlinedTextField(
                        value = instructions,
                        onValueChange = { viewModel.onInstructionsChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        placeholder = {
                            Text(
                                text = "1. Enjuagar la quinoa...\n2. Picar las verduras...\n3. Mezclar todo en un bowl...",
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

                // Error Message
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
                                text = uiState.errorMessage ?: "",
                                color = Color(0xFFEF4444),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Save Button
                Button(
                    onClick = { viewModel.createRecipe() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoading && name.isNotBlank() &&
                             description.isNotBlank() && ingredients.isNotBlank() &&
                             instructions.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981),
                        disabledContainerColor = Color(0xFF10B981).copy(alpha = 0.5f)
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
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
                                text = "Guardar Receta",
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
