package com.alilopez.kt_demohilt.core.database.entities

import androidx.room.Entity

@Entity(primaryKeys = ["planId", "recipeId"])
data class RecipePlanRecipeCrossRef(
    val planId: Int,
    val recipeId: Int
)
