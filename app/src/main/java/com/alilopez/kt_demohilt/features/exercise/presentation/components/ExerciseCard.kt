package com.alilopez.kt_demohilt.features.exercise.presentation.components

import android.content.Context
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.util.DebugLogger

fun gifImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .logger(DebugLogger())
        .build()
}

@Composable
fun ExerciseCard(
    name: String,
    imageUrl: String,
    modifier: Modifier = Modifier,
    compactMode: Boolean = false,
    instructions: List<String>,
    isLocal: Boolean = false,
    exerciseType: String? = null,
    difficulty: String? = null,
    accentColor: Color = Color(0xFF10B981),
    currentUserId: Int? = null,
    exerciseUserId: Int? = null,
    offlineAvailable: Boolean = false,
    onClick: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onDownload: (() -> Unit)? = null,
    onRemoveClick: (() -> Unit)? = null
) {
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val cardBackgroundColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val secondaryTextColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)

    val isOwner = currentUserId != null && exerciseUserId != null && currentUserId == exerciseUserId

    val card: @Composable (Modifier, @Composable () -> Unit) -> Unit = { cardModifier, content ->
        if (onClick != null) {
            Card(
                onClick = onClick,
                modifier = cardModifier,
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
            ) { content() }
        } else {
            Card(
                modifier = cardModifier,
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
            ) { content() }
        }
    }

    card(
        modifier
            .fillMaxWidth()
            .then(if (compactMode) Modifier.height(360.dp) else Modifier)
            .padding(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        imageLoader = gifImageLoader(LocalContext.current),
                        contentDescription = "Imagen de $name",
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Sin imagen",
                            tint = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = textColor,
                            modifier = Modifier.weight(1f)
                        )

                        if (isOwner) {
                            if (onEdit != null) {
                                IconButton(onClick = onEdit) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar ejercicio",
                                        tint = accentColor
                                    )
                                }
                            }
                            if (onDelete != null) {
                                IconButton(onClick = onDelete) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar ejercicio",
                                        tint = Color(0xFFEF4444)
                                    )
                                }
                            }
                        }
                        
                        if (onDownload != null) {
                            IconButton(onClick = onDownload) {
                                Icon(
                                    imageVector = if (offlineAvailable) Icons.Default.DownloadDone else Icons.Default.Download,
                                    contentDescription = if (offlineAvailable) "Disponible offline" else "Descargar ejercicio",
                                    tint = if (offlineAvailable) accentColor else secondaryTextColor
                                )
                            }
                        }
                    }

                    // Badges para ejercicios locales
                    if (isLocal) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (!exerciseType.isNullOrBlank()) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            accentColor.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = exerciseType,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = accentColor
                                    )
                                }
                            }
                            if (!difficulty.isNullOrBlank()) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            accentColor.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = difficulty,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = accentColor
                                    )
                                }
                            }
                        }
                    }

                    if (instructions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Instrucciones:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = accentColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        instructions.forEach { instruction ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(accentColor, shape = CircleShape)
                                        .align(Alignment.CenterVertically)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = instruction,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    color = secondaryTextColor
                                )
                            }
                        }
                    }
                }
            }

            // Botón de eliminar (X roja) si se proporciona onRemoveClick
            if (onRemoveClick != null) {
                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color.Red.copy(alpha = 0.8f), CircleShape)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Quitar de la lista",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
