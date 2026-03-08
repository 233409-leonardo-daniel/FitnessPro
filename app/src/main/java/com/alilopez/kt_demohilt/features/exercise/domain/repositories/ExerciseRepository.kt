package com.alilopez.kt_demohilt.features.exercise.domain.repositories

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise

interface ExerciseRepository {
    suspend fun getExercises(): List<Exercise>
    suspend fun getExercisesByBodyPart(bodyPart: String): List<Exercise>
}
