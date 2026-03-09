package com.alilopez.kt_demohilt.features.recipies.presentation.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alilopez.kt_demohilt.core.components.InputFitness
import com.alilopez.kt_demohilt.features.recipies.presentation.viewmodels.RecipiesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(
    onNavigateBack: () -> Unit,
    viewModel: RecipiesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()

    // Colores uniformes con LoginScreen
    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val labelColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
    val textFieldBackground = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val textFieldBorder = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)
    val placeholderColor = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8)

    var recipeName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var mealType by remember { mutableStateOf("") }

    val daysOfWeek = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    var selectedDays by remember { mutableStateOf<List<String>>(emptyList()) }

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
                        value = recipeName,
                        onValueChange = { recipeName = it },
                        placeholder = "e.g. Grilled Salmon Salad"
                    )
                }

                // Meal Type
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
                    InputFitness(
                        value = mealType,
                        onValueChange = { mealType = it },
                        placeholder = "e.g. Cena, Almuerzo, Desayuno"
                    )
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
                                onCheckedChange = { checked ->
                                    selectedDays = if (checked) {
                                        selectedDays + day
                                    } else {
                                        selectedDays - day
                                    }
                                },
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
                        onValueChange = { description = it },
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
                        onValueChange = { ingredients = it },
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
                        onValueChange = { instructions = it },
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
                    onClick = {
                        viewModel.createRecipe(
                            name = recipeName,
                            description = description,
                            ingredients = ingredients,
                            instructions = instructions,
                            userId = 1,
                            scheduledDays = selectedDays,
                            mealType = mealType.ifEmpty { null }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoading && recipeName.isNotBlank() &&
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
