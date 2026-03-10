package com.alilopez.kt_demohilt.features.workoutplans.domain.usecases

import com.alilopez.kt_demohilt.features.workoutplans.domain.entities.WorkoutPlan
import com.alilopez.kt_demohilt.features.workoutplans.domain.repositories.WorkoutPlanRepository
import javax.inject.Inject

class CreateWorkoutPlanUseCase @Inject constructor(
    private val repository: WorkoutPlanRepository
) {
    suspend operator fun invoke(
        name: String, 
        description: String, 
        userId: Int,
        planType: String,
        isPrivate: Boolean
    ): Result<WorkoutPlan> {
        return try {
            Result.success(repository.createWorkoutPlan(name, description, userId, planType, isPrivate))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
