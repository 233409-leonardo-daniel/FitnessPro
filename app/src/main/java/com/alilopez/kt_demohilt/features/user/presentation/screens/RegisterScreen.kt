package com.alilopez.kt_demohilt.features.user.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alilopez.kt_demohilt.core.components.InputFitness
import com.alilopez.kt_demohilt.features.user.presentation.viewmodels.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()

    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardBackgroundColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White

    val email by viewModel.email.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val lastname by viewModel.lastname.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()

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
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
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

                Spacer(modifier = Modifier.height(8.dp))

                // Register Button
                Button(
                    onClick = { viewModel.onRegisterClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoading,
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
}
