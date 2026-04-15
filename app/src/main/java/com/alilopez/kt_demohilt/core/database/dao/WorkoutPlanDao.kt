package com.alilopez.kt_demohilt.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alilopez.kt_demohilt.core.database.entities.WorkoutPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutPlanDao {
    @Query("SELECT * FROM workout_plans WHERE isDownloaded = 1")
    fun getDownloadedWorkoutPlans(): Flow<List<WorkoutPlanEntity>>

    @Query("SELECT id FROM workout_plans")
    suspend fun getDownloadedPlanIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutPlan(plan: WorkoutPlanEntity)

    @Query("DELETE FROM workout_plans WHERE id = :planId")
    suspend fun deleteWorkoutPlan(planId: Int)
}
