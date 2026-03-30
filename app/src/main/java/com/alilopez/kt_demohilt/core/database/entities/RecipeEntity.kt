package com.alilopez.kt_demohilt.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val instructions: String,
    val ingredients: List<String>,
    val measures: List<String>,
    val imageUrl: String,
    val category: String,
    val area: String,
    val tags: List<String>,
    val youtubeUrl: String,
    val sourceUrl: String
)
