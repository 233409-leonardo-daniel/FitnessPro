package com.alilopez.kt_demohilt.features.workoutplans.domain.usecases

import com.alilopez.kt_demohilt.features.workoutplans.domain.repositories.WorkoutPlanRepository
import javax.inject.Inject

class DeleteWorkoutPlanUseCase @Inject constructor(
    private val repository: WorkoutPlanRepository
) {
    suspend operator fun invoke(planId: Int): Result<Unit> {
        return try {
            repository.deleteWorkoutPlan(planId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
