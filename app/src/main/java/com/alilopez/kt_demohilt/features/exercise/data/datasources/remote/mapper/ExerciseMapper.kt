package com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.mapper

import com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.model.ExercisesDto
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise

fun ExercisesDto.toDomain(): Exercise {
    return Exercise(
        exerciseId = this.exerciseId,
        name = this.name,
        gifUrl = this.gifUrl,
        targetMuscles  = this.targetMuscles,
        instructions = this.instructions
    )
}
