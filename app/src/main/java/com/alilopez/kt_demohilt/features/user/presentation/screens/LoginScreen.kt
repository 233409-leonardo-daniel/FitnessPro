package com.alilopez.kt_demohilt.features.user.presentation.screens

import android.content.Context
import android.util.Log
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alilopez.kt_demohilt.BuildConfig
import com.alilopez.kt_demohilt.R
import com.alilopez.kt_demohilt.core.components.InputFitness
import com.alilopez.kt_demohilt.features.user.presentation.viewmodels.LoginViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onClickLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToOffline: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardBackgroundColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White

    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val membership by viewModel.membership.collectAsStateWithLifecycle()
    val isPremium = membership?.trim()?.equals("premium", ignoreCase = true) == true

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onClickLogin()
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                )

                Text(
                    text = "Bienvenido a FitnessPro",
                    fontSize = 16.sp,
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                InputFitness(
                    value = email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    placeholder = "user@example.com",
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )

                InputFitness(
                    value = password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    placeholder = "Contraseña",
                    isPassword = true,
                    leadingIcon = Icons.Default.Lock,
                    keyboardType = KeyboardType.Password
                )

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = { viewModel.onLoginClick() },
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
                            text = "Iniciar Sesión",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                OutlinedButton(
                    onClick = {
                        scope.launch {
                            Log.d("GoogleLogin", "Botón presionado. Iniciando flujo...")
                            performGoogleLogin(context, viewModel)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isDarkTheme) Color.White else Color.Black
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continuar con Google",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                TextButton(
                    onClick = onNavigateToRegister,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "¿No tienes cuenta? Regístrate",
                        color = Color(0xFF10B981),
                        fontSize = 14.sp
                    )
                }

                if (isPremium) {
                    TextButton(
                        onClick = onNavigateToOffline,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Ver mis descargas (Modo Offline)",
                            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

private suspend fun performGoogleLogin(context: Context, viewModel: LoginViewModel) {
    val credentialManager = CredentialManager.create(context)
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
        .setAutoSelectEnabled(true)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        Log.d("GoogleLogin", "Lanzando selector de cuentas...")
        val result = credentialManager.getCredential(
            context = context,
            request = request
        )
        val credential = result.credential
        Log.d("GoogleLogin", "Credencial obtenida tipo: ${credential::class.java.simpleName}")

        when (credential) {
            is GoogleIdTokenCredential -> {
                Log.d("GoogleLogin", "ID Token obtenido directamente.")
                viewModel.onGoogleLoginSuccess(credential.idToken)
            }
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    Log.d("GoogleLogin", "ID Token extraído de CustomCredential.")
                    viewModel.onGoogleLoginSuccess(googleIdTokenCredential.idToken)
                } else {
                    Log.e("GoogleLogin", "CustomCredential de tipo desconocido: ${credential.type}")
                    viewModel.onGoogleLoginError("Tipo de credencial no reconocido")
                }
            }
            else -> {
                Log.e("GoogleLogin", "Tipo de credencial no soportado: ${credential::class.java.name}")
                viewModel.onGoogleLoginError("Credencial no soportada")
            }
        }
    } catch (e: GetCredentialException) {
        Log.e("GoogleLogin", "Fallo de CredentialManager: ${e.message}")
        viewModel.onGoogleLoginError("Error de Google: ${e.message}")
    } catch (e: Exception) {
        Log.e("GoogleLogin", "Error crítico: ${e.message}")
        viewModel.onGoogleLoginError("Error inesperado: ${e.message}")
    }
}
