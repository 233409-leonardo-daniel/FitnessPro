package com.alilopez.kt_demohilt.core.database.dao

import androidx.room.*
import com.alilopez.kt_demohilt.core.database.entities.ExerciseEntity
import com.alilopez.kt_demohilt.core.database.entities.WorkoutPlanEntity
import com.alilopez.kt_demohilt.core.database.entities.WorkoutPlanExerciseCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutPlanDao {
    @Query("SELECT * FROM workout_plans WHERE isDownloaded = 1")
    fun getDownloadedWorkoutPlans(): Flow<List<WorkoutPlanEntity>>

    @Query("SELECT id FROM workout_plans")
    suspend fun getDownloadedPlanIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutPlan(plan: WorkoutPlanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutPlanCrossRef(crossRef: WorkoutPlanExerciseCrossRef)

    @Transaction
    @Query("""
        SELECT * FROM exercises 
        INNER JOIN WorkoutPlanExerciseCrossRef ON exercises.id = WorkoutPlanExerciseCrossRef.exerciseId 
        WHERE WorkoutPlanExerciseCrossRef.planId = :planId
    """)
    fun getExercisesForPlan(planId: Int): Flow<List<ExerciseEntity>>

    @Query("DELETE FROM workout_plans WHERE id = :planId")
    suspend fun deleteWorkoutPlan(planId: Int)

    @Query("DELETE FROM WorkoutPlanExerciseCrossRef WHERE planId = :planId")
    suspend fun deleteWorkoutPlanCrossRefs(planId: Int)
}
