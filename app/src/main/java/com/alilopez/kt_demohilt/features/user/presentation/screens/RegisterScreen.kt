package com.alilopez.kt_demohilt.features.user.presentation.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alilopez.kt_demohilt.core.components.InputFitness
import com.alilopez.kt_demohilt.features.user.presentation.viewmodels.RegisterViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()
    val activity = LocalActivity.current

    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardBackgroundColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)

    val email by viewModel.email.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val lastname by viewModel.lastname.collectAsStateWithLifecycle()
    val birthdate by viewModel.birthdate.collectAsStateWithLifecycle()
    val weight by viewModel.weight.collectAsStateWithLifecycle()
    val height by viewModel.height.collectAsStateWithLifecycle()
    val gender by viewModel.gender.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()

    var showDatePicker by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }
    var showGenderMenu by remember { mutableStateOf(false) }
    
    val datePickerState = rememberDatePickerState()
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    // Navegar cuando el registro sea exitoso
    LaunchedEffect(uiState.isRegistered) {
        if (uiState.isRegistered) {
            onRegisterSuccess()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(
                containerColor = cardBackgroundColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            @Suppress("DEPRECATION")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Título
                Text(
                    text = "Crear Cuenta",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Text(
                    text = "Únete a FitnessPro",
                    fontSize = 16.sp,
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Email Input
                InputFitness(
                    value = email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    placeholder = "Correo electrónico",
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )

                // Name Input
                InputFitness(
                    value = name,
                    onValueChange = { viewModel.onNameChange(it) },
                    placeholder = "Nombre",
                    leadingIcon = Icons.Default.Person,
                    keyboardType = KeyboardType.Text
                )

                // Lastname Input
                InputFitness(
                    value = lastname,
                    onValueChange = { viewModel.onLastnameChange(it) },
                    placeholder = "Apellido",
                    leadingIcon = Icons.Default.Person,
                    keyboardType = KeyboardType.Text
                )

                // Birthdate Input
                InputFitness(
                    value = birthdate,
                    onValueChange = {},
                    placeholder = "Fecha de nacimiento (YYYY-MM-DD)",
                    leadingIcon = Icons.Default.DateRange,
                    readOnly = true,
                    trailingIcon = Icons.Default.DateRange,
                    onTrailingIconClick = { showDatePicker = true },
                    modifier = Modifier.clickable { showDatePicker = true }
                )

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { selectedMillis ->
                                    viewModel.onBirthdateChange(dateFormatter.format(Date(selectedMillis)))
                                }
                                showDatePicker = false
                            }) {
                                Text("Aceptar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("Cancelar")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                InputFitness(
                    value = weight,
                    onValueChange = { viewModel.onWeightChange(it) },
                    placeholder = "Peso (kg)",
                    keyboardType = KeyboardType.Decimal
                )

                InputFitness(
                    value = height,
                    onValueChange = { viewModel.onHeightChange(it) },
                    placeholder = "Altura (cm)",
                    keyboardType = KeyboardType.Decimal
                )

                // Selector de Género
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Género") },
                        leadingIcon = { Icon(Icons.Default.Face, contentDescription = null) },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showGenderMenu = true },
                        enabled = false,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = textColor,
                            disabledBorderColor = if (isDarkTheme) Color(0xFF346544) else Color(0xFFE2E8F0),
                            disabledLeadingIconColor = if (isDarkTheme) Color(0xFF5e8c6d) else Color(0xFF94A3B8),
                            disabledTrailingIconColor = if (isDarkTheme) Color(0xFF5e8c6d) else Color(0xFF94A3B8),
                            disabledPlaceholderColor = if (isDarkTheme) Color(0xFF5e8c6d) else Color(0xFF94A3B8),
                            disabledContainerColor = if (isDarkTheme) Color(0xFF1a3222) else Color.White
                        )
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { showGenderMenu = true })
                    
                    DropdownMenu(
                        expanded = showGenderMenu,
                        onDismissRequest = { showGenderMenu = false },
                        modifier = Modifier.fillMaxWidth(0.7f)
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

                // Password Input
                InputFitness(
                    value = password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    placeholder = "Contraseña",
                    isPassword = true,
                    leadingIcon = Icons.Default.Lock,
                    keyboardType = KeyboardType.Password
                )

                // Error Message
                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = { checked ->
                            if (checked) {
                                showTermsDialog = true
                            } else {
                                termsAccepted = false
                            }
                        },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF10B981))
                    )
                    Text(
                        text = "Acepto los ",
                        fontSize = 13.sp,
                        color = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569)
                    )
                    TextButton(
                        onClick = { showTermsDialog = true },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Términos y Condiciones",
                            fontSize = 13.sp,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Register Button
                Button(
                    onClick = { viewModel.onRegisterClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoading && termsAccepted,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981)
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Registrarse",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Login Link
                TextButton(
                    onClick = onNavigateToLogin,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? Inicia sesión",
                        color = Color(0xFF10B981),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    if (showTermsDialog) {
        TermsAndConditionsDialog(
            onAccept = {
                termsAccepted = true
                showTermsDialog = false
            },
            onReject = {
                showTermsDialog = false
                activity?.finishAffinity()
            }
        )
    }
}
