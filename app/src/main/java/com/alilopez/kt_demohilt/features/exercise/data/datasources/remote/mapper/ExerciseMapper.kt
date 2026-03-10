package com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.mapper

import com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.model.ExercisesDto
import com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.model.LocalExerciseDto
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise

fun ExercisesDto.toDomain(): Exercise {
    return Exercise(
        exerciseId = this.exerciseId,
        name = this.name,
        gifUrl = this.gifUrl,
        targetMuscles = this.targetMuscles,
        instructions = this.instructions
    )
}

fun LocalExerciseDto.toDomain(): Exercise {
    return Exercise(
        exerciseId = this.id.toString(),
        name = this.name,
        gifUrl = this.imageUrl ?: "",
        targetMuscles = this.targetMuscles,
        instructions = if (this.instructions.isNullOrBlank()) emptyList() else listOf(this.instructions),
        description = this.description,
        userId = this.userId,
        scheduledDays = this.scheduledDays,
        bodyparts = this.bodyparts,
        equipments = this.equipments,
        secondaryMuscles = this.secondaryMuscles,
        exerciseType = this.exerciseType,
        difficulty = this.difficulty,
        private = true
    )
}

