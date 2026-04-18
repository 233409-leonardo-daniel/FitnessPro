package com.alilopez.kt_demohilt.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe_plans")
data class RecipePlanEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val userId: Int,
    val isPrivate: Boolean,
    val isDownloaded: Boolean = true // Solo se guardan en Room al descargar
)
