package com.alilopez.kt_demohilt.features.workoutplans.data.datasources.remote.mapper

import com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.workoutplans.data.datasources.remote.model.WorkoutPlanDto
import com.alilopez.kt_demohilt.features.workoutplans.domain.entities.WorkoutPlan

fun WorkoutPlanDto.toDomain(): WorkoutPlan {
    return WorkoutPlan(
        id = this.id,
        name = this.name,
        description = this.description,
        userId = this.userId,
        planType = this.planType,
        isPrivate = this.private,
        exercises = this.exercises.map { it.toDomain() }
    )
}
