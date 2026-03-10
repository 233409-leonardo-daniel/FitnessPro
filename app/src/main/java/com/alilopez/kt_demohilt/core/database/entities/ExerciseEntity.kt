package com.alilopez.kt_demohilt.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val user_id: Int,
    val scheduled_days: String,
    val image_url: String,
    val bodyparts: String,
    val equipments: String,
    val targetMuscles: String,
    val secondaryMuscles: String,
    val exercise_type: String,
    val instruccions: String,
    val difficulty: String
)