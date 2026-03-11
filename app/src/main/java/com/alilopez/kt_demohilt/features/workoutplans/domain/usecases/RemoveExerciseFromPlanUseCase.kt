package com.alilopez.kt_demohilt.features.workoutplans.domain.usecases

import com.alilopez.kt_demohilt.features.workoutplans.domain.entities.WorkoutPlan
import com.alilopez.kt_demohilt.features.workoutplans.domain.repositories.WorkoutPlanRepository
import javax.inject.Inject

class RemoveExerciseFromPlanUseCase @Inject constructor(
    private val repository: WorkoutPlanRepository
) {
    suspend operator fun invoke(planId: Int, exerciseId: Int): Result<WorkoutPlan> {
        return try {
            Result.success(repository.removeExerciseFromPlan(planId, exerciseId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
