package com.alilopez.kt_demohilt.features.workoutplans.domain.usecases

import com.alilopez.kt_demohilt.features.workoutplans.domain.entities.WorkoutPlan
import com.alilopez.kt_demohilt.features.workoutplans.domain.repositories.WorkoutPlanRepository
import javax.inject.Inject

class GetUserWorkoutPlansUseCase @Inject constructor(
    private val repository: WorkoutPlanRepository
) {
    suspend operator fun invoke(userId: Int): Result<List<WorkoutPlan>> {
        return try {
            Result.success(repository.getUserWorkoutPlans(userId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
