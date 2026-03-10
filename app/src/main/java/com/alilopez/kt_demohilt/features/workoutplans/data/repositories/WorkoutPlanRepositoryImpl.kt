package com.alilopez.kt_demohilt.features.workoutplans.data.repositories

import com.alilopez.kt_demohilt.core.network.FitnessProApi
import com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.workoutplans.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.workoutplans.data.datasources.remote.model.AddExerciseToPlanDto
import com.alilopez.kt_demohilt.features.workoutplans.data.datasources.remote.model.WorkoutPlanCreateDto
import com.alilopez.kt_demohilt.features.workoutplans.domain.entities.WorkoutPlan
import com.alilopez.kt_demohilt.features.workoutplans.domain.repositories.WorkoutPlanRepository
import javax.inject.Inject

class WorkoutPlanRepositoryImpl @Inject constructor(
    private val api: FitnessProApi
) : WorkoutPlanRepository {

    override suspend fun getUserWorkoutPlans(userId: Int): List<WorkoutPlan> {
        return api.getUserWorkoutPlans(userId).map { it.toDomain() }
    }

    override suspend fun createWorkoutPlan(
        name: String, 
        description: String, 
        userId: Int,
        planType: String,
        isPrivate: Boolean
    ): WorkoutPlan {
        val createDto = WorkoutPlanCreateDto(
            name = name, 
            description = description, 
            userId = userId,
            planType = planType,
            private = isPrivate
        )
        return api.createWorkoutPlan(createDto).toDomain()
    }

    override suspend fun addExerciseToPlan(planId: Int, exerciseId: Int): WorkoutPlan {
        val addDto = AddExerciseToPlanDto(exerciseId = exerciseId)
        return api.addExerciseToPlan(planId, addDto).toDomain()
    }

    override suspend fun getPlanExercises(planId: Int): List<Exercise> {
        return api.getPlanExercises(planId).map { it.toDomain() }
    }

    override suspend fun deleteWorkoutPlan(planId: Int) {
        api.deleteWorkoutPlan(planId)
    }

    override suspend fun removeExerciseFromPlan(planId: Int, exerciseId: Int): WorkoutPlan {
        return api.removeExerciseFromPlan(planId, exerciseId).toDomain()
    }
}
