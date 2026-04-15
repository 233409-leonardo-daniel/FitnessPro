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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExercisesIgnore(exercises: List<ExerciseEntity>): List<Long>

    @androidx.room.Update
    suspend fun updateExercises(exercises: List<ExerciseEntity>)

    @androidx.room.Transaction
    suspend fun insertOrUpdateExercises(exercises: List<ExerciseEntity>) {
        val existingOfflineIds = getOfflineExerciseIds()
        val insertResults = insertExercisesIgnore(exercises)
        val updateList = mutableListOf<ExerciseEntity>()
        
        for (i in insertResults.indices) {
            val isIgnored = insertResults[i] == -1L
            val exercise = exercises[i]
            if (isIgnored) {
                // Ya existe, necesitamos actualizar conservando offline_available si lo estaba
                if (existingOfflineIds.contains(exercise.id)) {
                    updateList.add(exercise.copy(offline_available = true))
                } else {
                    updateList.add(exercise)
                }
            } else {
                // Fue insertado por primera vez. Si el backend mandó offline_available = true (poco probable),
                // o si por alguna razón estaba en la lista de IDs (casi imposible pero seguro).
                if (existingOfflineIds.contains(exercise.id)) {
                    updateOfflineAvailable(exercise.id, true)
                }
            }
        }
        
        if (updateList.isNotEmpty()) {
            updateExercises(updateList)
        }
    }

    @Query("SELECT id FROM exercises WHERE offline_available = 1")
    suspend fun getOfflineExerciseIds(): List<Int>

    // Deprecated for direct use, use insertOrUpdateExercises to preserve offline state
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