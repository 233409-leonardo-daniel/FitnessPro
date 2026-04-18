package com.alilopez.kt_demohilt.features.workoutplans.domain.usecases

import com.alilopez.kt_demohilt.features.workoutplans.domain.entities.WorkoutPlan
import com.alilopez.kt_demohilt.features.workoutplans.domain.repositories.WorkoutPlanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDownloadedWorkoutPlansUseCase @Inject constructor(
    private val repository: WorkoutPlanRepository
) {
    operator fun invoke(): Flow<List<WorkoutPlan>> = repository.getDownloadedWorkoutPlans()
}
