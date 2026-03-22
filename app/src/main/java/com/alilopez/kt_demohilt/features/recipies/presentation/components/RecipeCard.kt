package com.alilopez.kt_demohilt.features.recipies.presentation.components

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe

@Composable
fun RecipeCard(
    recipe: Recipe,
    modifier: Modifier = Modifier,
    compactMode: Boolean = false,
    currentUserId: Int? = null,
    onClick: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val isOwner = currentUserId != null && recipe.userId == currentUserId
    val isDarkTheme = isSystemInDarkTheme()

    val cardBackgroundColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val secondaryTextColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
    val accentColor = Color(0xFF10B981)
    val instructionBoxColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)

    var isPlaying by remember(recipe.id) { mutableStateOf(false) }
    var mediaPlayer by remember(recipe.id) { mutableStateOf<MediaPlayer?>(null) }

    fun releasePlayer() {
        mediaPlayer?.run {
            try {
                stop()
            } catch (_: Exception) {
                // Ignore invalid state while stopping async playback.
            }
            release()
        }
        mediaPlayer = null
        isPlaying = false
    }

    DisposableEffect(recipe.id) {
        onDispose { releasePlayer() }
    }

    val ingredientsList = recipe.ingredients
        .split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }
    val visibleIngredients = if (compactMode) ingredientsList.take(3) else ingredientsList

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
    ) {
        Column {
            Box {
                if (!recipe.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(recipe.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = recipe.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))
                    )
                }

                if (!recipe.mealType.isNullOrBlank()) {
                    Surface(
                        color = accentColor.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = recipe.mealType,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = recipe.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (!recipe.audioUrl.isNullOrBlank()) {
                        IconButton(onClick = {
                            if (isPlaying) {
                                releasePlayer()
                            } else {
                                val player = MediaPlayer()
                                mediaPlayer = player
                                try {
                                    player.setDataSource(recipe.audioUrl)
                                    player.setOnPreparedListener {
                                        it.start()
                                        isPlaying = true
                                    }
                                    player.setOnCompletionListener {
                                        it.release()
                                        mediaPlayer = null
                                        isPlaying = false
                                    }
                                    player.setOnErrorListener { mp, _, _ ->
                                        mp.release()
                                        mediaPlayer = null
                                        isPlaying = false
                                        true
                                    }
                                    player.prepareAsync()
                                } catch (_: Exception) {
                                    player.release()
                                    mediaPlayer = null
                                    isPlaying = false
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = if (isPlaying) "Detener audio" else "Reproducir audio",
                                tint = if (isPlaying) Color(0xFF3B82F6) else accentColor
                            )
                        }
                    }

                    if (isOwner) {
                        IconButton(onClick = onEdit) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar receta",
                                tint = accentColor
                            )
                        }

                        IconButton(onClick = onDelete) {
                            Text(
                                text = "X",
                                color = Color(0xFFEF4444),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = recipe.description,
                    fontSize = 14.sp,
                    color = secondaryTextColor,
                    lineHeight = 20.sp,
                    maxLines = if (compactMode) 2 else Int.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ingredientes",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                visibleIngredients.forEach { ingredient ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(accentColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = ingredient,
                            fontSize = 13.sp,
                            color = secondaryTextColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                if (compactMode && ingredientsList.size > visibleIngredients.size) {
                    Text(
                        text = "+${ingredientsList.size - visibleIngredients.size} mas",
                        fontSize = 12.sp,
                        color = secondaryTextColor
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = instructionBoxColor
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "INSTRUCCIONES",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = secondaryTextColor
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = recipe.instructions,
                            fontSize = 13.sp,
                            color = secondaryTextColor,
                            lineHeight = 18.sp,
                            maxLines = if (compactMode) 4 else Int.MAX_VALUE,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = secondaryTextColor,
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        text = recipe.scheduledDays.joinToString(", "),
                        fontSize = 12.sp,
                        color = secondaryTextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
