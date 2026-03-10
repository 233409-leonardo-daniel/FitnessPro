package com.alilopez.kt_demohilt.features.exercise.data.datasources.local.mapper

import com.alilopez.kt_demohilt.core.database.entities.ExerciseEntity
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise

fun ExerciseEntity.toDomain() = Exercise(
    id = id,
    name = name,
    gifUrl = image_url,
    targetMuscles = targetMuscles.split(",").map { it.trim() },
    instructions = instruccions.split(",").map { it.trim() },
    description = description,
    userId = user_id,
    scheduledDays = scheduled_days.split(",").map { it.trim() },
    bodyparts = bodyparts.split(",").map { it.trim() },
    equipments = equipments.split(",").map { it.trim() },
    secondaryMuscles = secondaryMuscles.split(",").map { it.trim() },
    exerciseType = exercise_type,
    difficulty = difficulty,
    private = private
)

fun Exercise.toEntity() = ExerciseEntity(
    id = id ?: exerciseId?.hashCode() ?: 0,
    name = name,
    description = description ?: "",
    user_id = userId ?: 0,
    scheduled_days = scheduledDays.joinToString(","),
    image_url = gifUrl,
    bodyparts = bodyparts.joinToString(","),
    equipments = equipments.joinToString(","),
    targetMuscles = targetMuscles.joinToString(","),
    secondaryMuscles = secondaryMuscles.joinToString(","),
    exercise_type = exerciseType ?: "",
    instruccions = instructions.joinToString(","),
    difficulty = difficulty ?: "",
    private = private
)

