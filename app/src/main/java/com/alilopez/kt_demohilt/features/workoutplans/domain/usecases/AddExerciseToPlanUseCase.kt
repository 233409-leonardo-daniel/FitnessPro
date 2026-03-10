package com.alilopez.kt_demohilt.features.workoutplans.domain.usecases

import com.alilopez.kt_demohilt.features.workoutplans.domain.entities.WorkoutPlan
import com.alilopez.kt_demohilt.features.workoutplans.domain.repositories.WorkoutPlanRepository
import javax.inject.Inject

class AddExerciseToPlanUseCase @Inject constructor(
    private val repository: WorkoutPlanRepository
) {
    suspend operator fun invoke(planId: Int, exerciseId: Int): Result<WorkoutPlan> {
        return try {
            Result.success(repository.addExerciseToPlan(planId, exerciseId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
