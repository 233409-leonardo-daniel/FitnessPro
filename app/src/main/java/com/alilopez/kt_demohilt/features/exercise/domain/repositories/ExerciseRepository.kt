package com.alilopez.kt_demohilt.features.exercise.domain.repositories

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ExerciseRepository {
    suspend fun getExercises(): List<Exercise>
    suspend fun getExercisesByBodyPart(bodyPart: String): List<Exercise>
    fun getLocalExercises(): Flow<List<Exercise>>
    suspend fun createLocalExercise(
        name: String,
        description: String,
        userId: Int,
        scheduledDays: List<String>,
        bodyparts: List<String>,
        equipment: List<String>,
        targetMuscles: List<String>,
        secondaryMuscles: List<String>,
        exerciseType: String?,
        instructions: String?,
        difficulty: String,
        imageFile: File?
    ): Exercise
    suspend fun syncExercises()
}
