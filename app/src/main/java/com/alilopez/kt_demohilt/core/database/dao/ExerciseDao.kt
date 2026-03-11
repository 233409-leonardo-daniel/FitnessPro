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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)

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