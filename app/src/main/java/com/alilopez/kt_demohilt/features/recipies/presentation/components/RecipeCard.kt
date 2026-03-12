package com.alilopez.kt_demohilt.features.recipies.presentation.components

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val dividerColor = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Column {

            // IMAGE + BADGE
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
                }

                if (!recipe.mealType.isNullOrBlank()) {
                    Surface(
                        color = accentColor,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = recipe.mealType,
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(18.dp)
            ) {

                // TITLE + ACTIONS
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = recipe.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.weight(1f)
                    )

                    // Botón reproducir audio
                    if (!recipe.audioUrl.isNullOrBlank()) {
                        var isPlaying by remember { mutableStateOf(false) }

                        IconButton(onClick = {
                            if (!isPlaying) {
                                isPlaying = true
                                val mediaPlayer = MediaPlayer()
                                try {
                                    mediaPlayer.setDataSource(recipe.audioUrl)
                                    mediaPlayer.setOnPreparedListener { it.start() }
                                    mediaPlayer.setOnCompletionListener {
                                        isPlaying = false
                                        it.release()
                                    }
                                    mediaPlayer.setOnErrorListener { mp, _, _ ->
                                        isPlaying = false
                                        mp.release()
                                        true
                                    }
                                    mediaPlayer.prepareAsync()
                                } catch (e: Exception) {
                                    isPlaying = false
                                    mediaPlayer.release()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Reproducir audio",
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
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar receta",
                                tint = Color(0xFFEF4444)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // DESCRIPTION
                Text(
                    text = recipe.description,
                    fontSize = 14.sp,
                    color = secondaryTextColor,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // INGREDIENTES TITLE
                Text(
                    text = "Ingredientes",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                val ingredientsList = recipe.ingredients.split(",")

                ingredientsList.forEach {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(accentColor)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = it.trim(),
                            fontSize = 13.sp,
                            color = secondaryTextColor
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // INSTRUCTIONS BOX
                Surface(
                    color = if (isDarkTheme) Color(0xFF334155) else Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {

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
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(color = dividerColor)

                Spacer(modifier = Modifier.height(10.dp))

                // FOOTER
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = secondaryTextColor,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = recipe.scheduledDays.joinToString(", "),
                        fontSize = 12.sp,
                        color = secondaryTextColor
                    )
                }
            }
        }
    }
}
