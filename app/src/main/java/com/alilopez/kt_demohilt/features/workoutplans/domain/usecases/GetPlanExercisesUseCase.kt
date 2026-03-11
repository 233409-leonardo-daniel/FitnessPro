package com.alilopez.kt_demohilt.features.workoutplans.domain.usecases

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.workoutplans.domain.repositories.WorkoutPlanRepository
import javax.inject.Inject

class GetPlanExercisesUseCase @Inject constructor(
    private val repository: WorkoutPlanRepository
) {
    suspend operator fun invoke(planId: Int): Result<List<Exercise>> {
        return try {
            Result.success(repository.getPlanExercises(planId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
