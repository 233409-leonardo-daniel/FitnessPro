package com.alilopez.kt_demohilt.features.user.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardBackgroundColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val secondaryTextColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
    val accentColor = Color(0xFF10B981)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Términos y Condiciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = cardBackgroundColor,
                    titleContentColor = textColor,
                    navigationIconContentColor = textColor
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Términos y Condiciones de Uso",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "FitnessPro — Versión 1.0 | Última actualización: 30 de marzo de 2026",
                        fontSize = 12.sp,
                        color = secondaryTextColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF0FDF4)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Importante: Lea detenidamente estos Términos y Condiciones antes de utilizar FitnessPro. Al registrarse o usar la aplicación, usted acepta quedar vinculado por estos términos.",
                            fontSize = 12.sp,
                            color = Color(0xFF065F46),
                            modifier = Modifier.padding(12.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Sections
            TermsSection(
                title = "1. Identificación del responsable",
                content = "FitnessPro es desarrollada y operada para apoyar al usuario en la gestión de su plan de alimentación, entrenamiento físico y seguimiento de su progreso personal.",
                secondaryTextColor = secondaryTextColor,
                accentColor = accentColor
            )

            TermsSection(
                title = "2. Aceptación de los términos",
                content = "El uso de FitnessPro implica la aceptación plena y sin reservas de todos los términos aquí establecidos. Estos términos pueden ser actualizados periódicamente.",
                secondaryTextColor = secondaryTextColor,
                accentColor = accentColor
            )

            TermsSection(
                title = "3. Descripción del servicio",
                content = "FitnessPro ofrece funcionalidades para:\n• Planificación y seguimiento de comidas diarias\n• Gestión de rutinas de entrenamiento físico\n• Registro de ingredientes e información nutricional\n• Programación semanal con recordatorios\n• Visualización del historial de progreso\n\nLa Aplicación es una herramienta de apoyo y no reemplaza la consulta con profesionales de la salud.",
                secondaryTextColor = secondaryTextColor,
                accentColor = accentColor
            )

            TermsSection(
                title = "4. Registro y cuenta de usuario",
                content = "Para acceder debe crear una cuenta personal. Se compromete a:\n• Proporcionar información veraz y actualizada\n• Mantener la confidencialidad de sus credenciales\n• Notificar ante cualquier uso no autorizado\n• No ceder su cuenta a terceros",
                secondaryTextColor = secondaryTextColor,
                accentColor = accentColor
            )

            TermsSection(
                title = "5. Uso adecuado de la aplicación",
                content = "Está prohibido:\n• Utilizar para actividades ilegales\n• Intentar acceder sin autorización a sistemas ajenos\n• Reproducir o distribuir sin autorización\n• Introducir virus o malware\n• Realizar ingeniería inversa",
                secondaryTextColor = secondaryTextColor,
                accentColor = accentColor
            )

            TermsSection(
                title = "6. Aviso de salud",
                content = "FitnessPro es una herramienta de organización personal. Antes de iniciar cualquier plan de alimentación o rutina de ejercicio, consulte con un médico, nutricionista o entrenador certificado. El usuario asume toda responsabilidad sobre el uso de la información.",
                secondaryTextColor = secondaryTextColor,
                accentColor = accentColor
            )

            TermsSection(
                title = "7. Privacidad y datos personales",
                content = "Recopilamos:\n• Datos de registro (nombre, email, contraseña cifrada)\n• Datos de uso (planes, rutinas, historial)\n• Datos técnicos (SO, modelo de dispositivo)\n\nNo vendemos ni compartimos datos sin consentimiento.",
                secondaryTextColor = secondaryTextColor,
                accentColor = accentColor
            )

            TermsSection(
                title = "8. Propiedad intelectual",
                content = "Todo el contenido de FitnessPro es propiedad exclusiva y está protegido por leyes de propiedad intelectual. Queda prohibida su reproducción sin autorización.",
                secondaryTextColor = secondaryTextColor,
                accentColor = accentColor
            )

            TermsSection(
                title = "9. Disponibilidad del servicio",
                content = "No garantizamos disponibilidad ininterrumpida. Podrán realizarse actualizaciones y mantenimientos en cualquier momento sin previo aviso.",
                secondaryTextColor = secondaryTextColor,
                accentColor = accentColor
            )

            TermsSection(
                title = "10. Suspensión de cuenta",
                content = "Podemos suspender o cancelar acceso por:\n• Incumplimiento de estos términos\n• Uso fraudulento o malintencionado\n• Solicitud del usuario",
                secondaryTextColor = secondaryTextColor,
                accentColor = accentColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "FitnessPro © 2026 — Todos los derechos reservados",
                fontSize = 12.sp,
                color = secondaryTextColor,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun TermsSection(
    title: String,
    content: String,
    secondaryTextColor: Color,
    accentColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme()) Color(0xFF1A2E3F) else Color(0xFFF0F9FF)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = content,
                fontSize = 12.sp,
                color = secondaryTextColor,
                lineHeight = 18.sp
            )
        }
    }
}

