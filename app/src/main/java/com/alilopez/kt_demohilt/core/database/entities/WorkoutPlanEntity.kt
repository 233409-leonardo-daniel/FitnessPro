package com.alilopez.kt_demohilt.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_plans")
data class WorkoutPlanEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val userId: Int,
    val planType: String,
    val isPrivate: Boolean,
    val isDownloaded: Boolean = true
)
