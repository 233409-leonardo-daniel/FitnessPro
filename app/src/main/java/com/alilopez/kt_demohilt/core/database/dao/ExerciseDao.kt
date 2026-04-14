package com.alilopez.kt_demohilt.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alilopez.kt_demohilt.core.database.entities.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE user_id = :userId")
    fun getExercisesByUserId(userId: Int): Flow<List<ExerciseEntity>>

    @Query("UPDATE exercises SET offline_available = :isAvailable WHERE id = :exerciseId")
    suspend fun updateOfflineAvailable(exerciseId: Int, isAvailable: Boolean)

    @Query("SELECT * FROM exercises WHERE offline_available = 1")
    fun getOfflineExercises(): Flow<List<ExerciseEntity>>

    @Query("UPDATE exercises SET offline_available = 0 WHERE offline_available = 1")
    suspend fun clearAllOfflineExercises()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)

    @Query("DELETE FROM exercises WHERE user_id = :userId")
    suspend fun deleteExercisesByUserId(userId: Int)

    @Query("""
        SELECT * FROM exercises
        WHERE (:bodyPart IS NULL OR bodyparts LIKE '%' || :bodyPart || '%')
        AND (:difficulty IS NULL OR difficulty = :difficulty)
        AND (:exerciseType IS NULL OR exercise_type = :exerciseType)
    """)
    fun getExercisesByFilter(
        bodyPart: String?,
        difficulty: String?,
        exerciseType: String?
    ): Flow<List<ExerciseEntity>>
}