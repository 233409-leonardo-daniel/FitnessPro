package com.alilopez.kt_demohilt.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val premiumColor = Color(0xFFF59E0B)

@Composable
fun PremiumGateContent(
    onNavigateToPremium: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Stars,
            contentDescription = null,
            tint = premiumColor,
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Contenido Premium",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = premiumColor
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Suscríbete a FitnessPro Premium para acceder a este contenido exclusivo.",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))
        Button(
            onClick = onNavigateToPremium,
            colors = ButtonDefaults.buttonColors(containerColor = premiumColor)
        ) {
            Text(
                text = "Ver planes Premium",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
