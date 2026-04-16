package com.alilopez.kt_demohilt.features.workoutplans.domain.repositories

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.workoutplans.domain.entities.WorkoutPlan
import kotlinx.coroutines.flow.Flow

interface WorkoutPlanRepository {
    suspend fun getUserWorkoutPlans(userId: Int): List<WorkoutPlan>
    suspend fun createWorkoutPlan(
        name: String, 
        description: String, 
        userId: Int,
        planType: String,
        isPrivate: Boolean
    ): WorkoutPlan
    suspend fun addExerciseToPlan(planId: Int, exerciseId: Int): WorkoutPlan
    suspend fun getPlanExercises(planId: Int): List<Exercise>
    suspend fun deleteWorkoutPlan(planId: Int)
    suspend fun removeExerciseFromPlan(planId: Int, exerciseId: Int): WorkoutPlan
    
    // Para modo offline
    fun getDownloadedWorkoutPlans(): Flow<List<WorkoutPlan>>
    suspend fun getDownloadedPlanIds(): List<Int>
    suspend fun removeLocalWorkoutPlanDownload(planId: Int)
}
