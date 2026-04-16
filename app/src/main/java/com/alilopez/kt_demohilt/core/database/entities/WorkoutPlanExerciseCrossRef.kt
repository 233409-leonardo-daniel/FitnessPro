package com.alilopez.kt_demohilt.core.database.entities

import androidx.room.Entity

@Entity(primaryKeys = ["planId", "exerciseId"])
data class WorkoutPlanExerciseCrossRef(
    val planId: Int,
    val exerciseId: Int
)
