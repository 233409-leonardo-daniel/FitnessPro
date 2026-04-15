package com.alilopez.kt_demohilt.features.workoutplans.data.repositories

import com.alilopez.kt_demohilt.core.database.dao.WorkoutPlanDao
import com.alilopez.kt_demohilt.core.network.FitnessProApi
import com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.workoutplans.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.workoutplans.data.datasources.remote.model.AddExerciseToPlanDto
import com.alilopez.kt_demohilt.features.workoutplans.data.datasources.remote.model.WorkoutPlanCreateDto
import com.alilopez.kt_demohilt.features.workoutplans.domain.entities.WorkoutPlan
import com.alilopez.kt_demohilt.features.workoutplans.domain.repositories.WorkoutPlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkoutPlanRepositoryImpl @Inject constructor(
    private val api: FitnessProApi,
    private val workoutPlanDao: WorkoutPlanDao
) : WorkoutPlanRepository {

    override suspend fun getUserWorkoutPlans(userId: Int): List<WorkoutPlan> {
        val remotePlans = api.getUserWorkoutPlans(userId).map { it.toDomain() }
        val downloadedIds = workoutPlanDao.getDownloadedPlanIds().toSet()
        
        return remotePlans.map { plan ->
            if (downloadedIds.contains(plan.id)) {
                plan.copy(isDownloaded = true)
            } else {
                plan
            }
        }
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
        workoutPlanDao.deleteWorkoutPlan(planId)
    }

    override suspend fun removeExerciseFromPlan(planId: Int, exerciseId: Int): WorkoutPlan {
        return api.removeExerciseFromPlan(planId, exerciseId).toDomain()
    }

    override fun getDownloadedWorkoutPlans(): Flow<List<WorkoutPlan>> {
        return workoutPlanDao.getDownloadedWorkoutPlans().map { entities ->
            entities.map { it.toDomain().copy(isDownloaded = true) }
        }
    }

    override suspend fun getDownloadedPlanIds(): List<Int> {
        return workoutPlanDao.getDownloadedPlanIds()
    }
}
