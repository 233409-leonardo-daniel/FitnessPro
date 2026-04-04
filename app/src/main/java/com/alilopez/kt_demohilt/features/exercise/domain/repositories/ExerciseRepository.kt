package com.alilopez.kt_demohilt.features.exercise.domain.repositories

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.domain.entities.ExerciseFilter
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ExerciseRepository {
    suspend fun getRemoteExercises(): List<Exercise>
    suspend fun getRemoteExercisesByBodyPart(bodyPart: String): List<Exercise>
    suspend fun searchLocalExercisesByName(name: String): List<Exercise>
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
    suspend fun syncExercisesByFilter(filter: ExerciseFilter)
    fun getLocalExercisesByFilter(filter: ExerciseFilter): Flow<List<Exercise>>
    suspend fun getExercisesByUserId(userId: Int): List<Exercise>
    suspend fun getCommunityExercises(userId: Int): List<Exercise>
    suspend fun getExerciseById(exerciseId: Int): Exercise
    suspend fun deleteExercise(exerciseId: Int)
}
