package com.alilopez.kt_demohilt.features.exercise.domain.repositories

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.domain.entities.ExerciseFilter
import com.alilopez.kt_demohilt.features.exercise.domain.entities.PaginatedExercises
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ExerciseRepository {
    suspend fun getRemoteExercises(
        limit: Int = 25,
        offset: Int? = null
    ): PaginatedExercises
    
    suspend fun getRemoteExercisesByBodyPart(
        bodyPart: String,
        limit: Int = 25,
        offset: Int? = null
    ): PaginatedExercises
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
    suspend fun saveExerciseForOffline(exerciseId: Int)
    suspend fun updateOfflineAvailable(exerciseId: Int, isAvailable: Boolean)
    suspend fun removeOfflineExercise(exerciseId: Int)
    fun getOfflineExercises(): Flow<List<Exercise>>
    suspend fun clearAllOfflineExercises()
    suspend fun updateLocalExercise(
        exerciseId: Int,
        name: String,
        description: String,
        scheduledDays: List<String>,
        bodyparts: List<String>,
        equipment: List<String>,
        targetMuscles: List<String>,
        secondaryMuscles: List<String>,
        exerciseType: String?,
        instructions: String?,
        difficulty: String,
        imageUrl: String?,
        imageFile: File?
    ): Exercise
    suspend fun deleteExercise(exerciseId: Int)
}
