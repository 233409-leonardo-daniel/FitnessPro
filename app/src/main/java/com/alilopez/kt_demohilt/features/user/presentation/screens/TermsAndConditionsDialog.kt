package com.alilopez.kt_demohilt.features.user.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun TermsAndConditionsDialog(
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val secondaryTextColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
    val accentColor = Color(0xFF10B981)

    Dialog(
        onDismissRequest = onReject,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp)
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(accentColor)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Términos y Condiciones",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = "FitnessPro — Versión 1.0 | Última actualización: 30 de marzo de 2026",
                        fontSize = 12.sp,
                        color = secondaryTextColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Importante: Lea detenidamente estos Términos y Condiciones antes de utilizar FitnessPro. Al registrarse o usar la aplicación, usted acepta quedar vinculado por estos términos. Si no está de acuerdo, no podrá acceder a los servicios de la aplicación.",
                        fontSize = 12.sp,
                        color = textColor,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    TermsDialogSection(
                        title = "1. Identificación del responsable",
                        content = "FitnessPro (en adelante \"la Aplicación\") es desarrollada y operada por [Nombre del desarrollador o empresa], con domicilio en [Dirección], y puede ser contactado en [correo@empresa.com]. La Aplicación está diseñada para dispositivos Android y tiene como propósito apoyar al usuario en la gestión de su plan de alimentación, entrenamiento físico y seguimiento de su progreso personal.",
                        secondaryTextColor = secondaryTextColor,
                        accentColor = accentColor
                    )

                    TermsDialogSection(
                        title = "2. Aceptación de los términos",
                        content = "El uso de FitnessPro implica la aceptación plena y sin reservas de todos los términos aquí establecidos. Estos términos pueden ser actualizados periódicamente. Cuando esto ocurra, se notificará al usuario dentro de la aplicación, y será necesaria una nueva aceptación para continuar usando el servicio.",
                        secondaryTextColor = secondaryTextColor,
                        accentColor = accentColor
                    )

                    TermsDialogSection(
                        title = "3. Descripción del servicio",
                        content = "FitnessPro ofrece las siguientes funcionalidades principales:\n\n- Planificación y seguimiento de comidas diarias por tipo (desayuno, almuerzo, cena, meriendas).\n- Gestión de rutinas de entrenamiento físico personalizadas.\n- Registro de ingredientes, instrucciones de preparación y valores nutricionales.\n- Programación semanal de comidas y ejercicios con recordatorios.\n- Visualización del historial de progreso físico y nutricional.\n- Días de descanso programados dentro del plan de entrenamiento.\n\nLa Aplicación es una herramienta de apoyo y organización personal. No reemplaza la consulta con profesionales de la salud, nutricionistas o entrenadores certificados.",
                        secondaryTextColor = secondaryTextColor,
                        accentColor = accentColor
                    )

                    TermsDialogSection(
                        title = "4. Registro y cuenta de usuario",
                        content = "Para acceder a las funciones de la Aplicación es necesario crear una cuenta personal. El usuario se compromete a:\n\n- Proporcionar información veraz, completa y actualizada durante el registro.\n- Mantener la confidencialidad de sus credenciales de acceso (correo y contraseña).\n- Notificar de inmediato al desarrollador ante cualquier uso no autorizado de su cuenta.\n- No ceder, vender ni transferir su cuenta a terceros.\n\nEl desarrollador no será responsable de los daños derivados del incumplimiento de estas obligaciones por parte del usuario.",
                        secondaryTextColor = secondaryTextColor,
                        accentColor = accentColor
                    )

                    TermsDialogSection(
                        title = "5. Uso adecuado de la aplicación",
                        content = "El usuario se compromete a utilizar FitnessPro únicamente para los fines personales y legítimos para los que fue diseñada. Está estrictamente prohibido:\n\n- Utilizar la Aplicación para actividades ilegales o contrarias a la moral.\n- Intentar acceder sin autorización a sistemas, bases de datos o cuentas ajenas.\n- Reproducir, distribuir, modificar o comercializar el contenido de la Aplicación sin autorización expresa.\n- Introducir virus, malware o cualquier código que pueda dañar el funcionamiento de la Aplicación.\n- Realizar ingeniería inversa sobre el código fuente de la Aplicación.",
                        secondaryTextColor = secondaryTextColor,
                        accentColor = accentColor
                    )

                    TermsDialogSection(
                        title = "6. Aviso de salud y limitación de responsabilidad médica",
                        content = "FitnessPro es una herramienta de organización y seguimiento personal. El contenido de la Aplicación, incluyendo planes de alimentación, rutinas de ejercicio y cualquier información nutricional, tiene carácter meramente orientativo.\n\nEl desarrollador no garantiza que los planes o rutinas sean adecuados para el estado de salud particular de cada usuario. Antes de iniciar cualquier plan de alimentación o rutina de ejercicio, se recomienda consultar con un médico, nutricionista o entrenador certificado. El usuario asume toda responsabilidad sobre el uso que haga de la información proporcionada por la Aplicación.",
                        secondaryTextColor = secondaryTextColor,
                        accentColor = accentColor
                    )

                    TermsDialogSection(
                        title = "7. Privacidad y tratamiento de datos personales",
                        content = "La Aplicación recopila y trata los siguientes datos del usuario con el único fin de prestar el servicio:\n\n- Datos de registro: nombre, correo electrónico y contraseña (cifrada).\n- Datos de uso: planes de comidas, rutinas de entrenamiento e historial de actividad registrado por el propio usuario.\n- Datos técnicos: versión del sistema operativo y modelo de dispositivo, para garantizar el correcto funcionamiento.\n\nLos datos del usuario no serán vendidos, cedidos ni compartidos con terceros sin consentimiento expreso, salvo que sea requerido por autoridad competente conforme a la ley aplicable. El usuario puede solicitar la eliminación de su cuenta y datos en cualquier momento desde los ajustes de la Aplicación.",
                        secondaryTextColor = secondaryTextColor,
                        accentColor = accentColor
                    )

                    TermsDialogSection(
                        title = "8. Propiedad intelectual",
                        content = "Todo el contenido de FitnessPro —incluyendo diseño, logotipo, código fuente, textos, gráficos e interfaz— es propiedad exclusiva del desarrollador o de sus licenciantes y está protegido por las leyes de propiedad intelectual aplicables. Queda prohibida su reproducción total o parcial sin autorización escrita previa.",
                        secondaryTextColor = secondaryTextColor,
                        accentColor = accentColor
                    )

                    TermsDialogSection(
                        title = "9. Disponibilidad del servicio",
                        content = "El desarrollador no garantiza la disponibilidad ininterrumpida de la Aplicación. Podrán realizarse actualizaciones, mantenimientos o interrupciones temporales del servicio en cualquier momento y sin previo aviso. El desarrollador no será responsable de los perjuicios que dichas interrupciones pudieran ocasionar al usuario.",
                        secondaryTextColor = secondaryTextColor,
                        accentColor = accentColor
                    )

                    TermsDialogSection(
                        title = "10. Modificaciones de los términos",
                        content = "El desarrollador se reserva el derecho de modificar estos Términos y Condiciones en cualquier momento. Las modificaciones entrarán en vigor desde su publicación dentro de la Aplicación. Si el usuario continúa usando FitnessPro tras la actualización, se entenderá que acepta los nuevos términos. En caso contrario, deberá dejar de utilizar la Aplicación.",
                        secondaryTextColor = secondaryTextColor,
                        accentColor = accentColor
                    )

                    TermsDialogSection(
                        title = "11. Suspensión y cancelación de cuenta",
                        content = "El desarrollador podrá suspender o cancelar el acceso de un usuario sin previo aviso en los siguientes casos:\n\n- Incumplimiento de cualquiera de estos Términos y Condiciones.\n- Uso fraudulento o malintencionado de la Aplicación.\n- Solicitud expresa del propio usuario.\n\nEl usuario podrá eliminar su cuenta en cualquier momento desde la sección de ajustes de la Aplicación.",
                        secondaryTextColor = secondaryTextColor,
                        accentColor = accentColor
                    )

                    TermsDialogSection(
                        title = "12. Legislación aplicable",
                        content = "Estos Términos y Condiciones se rigen por las leyes vigentes en [País / Estado donde se opera la app]. Cualquier controversia derivada del uso de la Aplicación será sometida a los tribunales competentes de dicha jurisdicción, salvo acuerdo en contrario entre las partes.",
                        secondaryTextColor = secondaryTextColor,
                        accentColor = accentColor
                    )

                    TermsDialogSection(
                        title = "13. Contacto",
                        content = "Para cualquier consulta, reclamación o solicitud relacionada con estos Términos y Condiciones, el usuario puede contactar al equipo de FitnessPro a través de:\n\n- Correo electrónico: [correo@fitnesspro.com]\n- Sitio web: [www.fitnesspro.com]\n\nFitnessPro © 2026 — Todos los derechos reservados.\nEste documento es de carácter informativo y legal. Se recomienda conservar una copia para referencia futura.",
                        secondaryTextColor = secondaryTextColor,
                        accentColor = accentColor
                    )
                }

                // Footer with buttons
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAccept,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Acepto los términos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Rechazar",
                            color = accentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TermsDialogSection(
    title: String,
    content: String,
    secondaryTextColor: Color,
    accentColor: Color
) {
    Column(
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            fontSize = 11.sp,
            color = secondaryTextColor,
            lineHeight = 17.sp
        )
    }
}




