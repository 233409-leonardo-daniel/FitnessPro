package com.alilopez.kt_demohilt.features.workoutplans.data.repositories

import com.alilopez.kt_demohilt.core.database.dao.WorkoutPlanDao
import com.alilopez.kt_demohilt.core.network.FitnessProApi
import com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.mapper.toDomain as exerciseToDomain
import com.alilopez.kt_demohilt.features.exercise.data.datasources.local.mapper.toDomain as exerciseToDomainLocal
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.workoutplans.data.datasources.remote.mapper.toDomain as planToDomain
import com.alilopez.kt_demohilt.features.workoutplans.data.datasources.remote.model.AddExerciseToPlanDto
import com.alilopez.kt_demohilt.features.workoutplans.data.datasources.remote.model.WorkoutPlanCreateDto
import com.alilopez.kt_demohilt.features.workoutplans.domain.entities.WorkoutPlan
import com.alilopez.kt_demohilt.features.workoutplans.domain.repositories.WorkoutPlanRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class WorkoutPlanRepositoryImpl @Inject constructor(
    private val api: FitnessProApi,
    private val workoutPlanDao: WorkoutPlanDao
) : WorkoutPlanRepository {

    override suspend fun getUserWorkoutPlans(userId: Int): List<WorkoutPlan> {
        return try {
            val remotePlans = api.getUserWorkoutPlans(userId).map { it.planToDomain() }
            val downloadedIds = workoutPlanDao.getDownloadedPlanIds().toSet()
            
            remotePlans.map { plan ->
                if (downloadedIds.contains(plan.id)) {
                    plan.copy(isDownloaded = true)
                } else {
                    plan
                }
            }
        } catch (e: Exception) {
            // Fallback Offline
            workoutPlanDao.getDownloadedWorkoutPlans().first().map { 
                it.planToDomain().copy(isDownloaded = true) 
            }
        }
    }

    override suspend fun createWorkoutPlan(name: String, description: String, userId: Int, planType: String, isPrivate: Boolean): WorkoutPlan {
        val createDto = WorkoutPlanCreateDto(name = name, description = description, userId = userId, planType = planType, private = isPrivate)
        return api.createWorkoutPlan(createDto).planToDomain()
    }

    override suspend fun addExerciseToPlan(planId: Int, exerciseId: Int): WorkoutPlan {
        val addDto = AddExerciseToPlanDto(exerciseId = exerciseId)
        return api.addExerciseToPlan(planId, addDto).planToDomain()
    }

    override suspend fun getPlanExercises(planId: Int): List<Exercise> {
        return try {
            api.getPlanExercises(planId).map { it.exerciseToDomain() }
        } catch (e: Exception) {
            // Fallback Offline: Leer de Room
            workoutPlanDao.getExercisesForPlan(planId).first().map { 
                it.exerciseToDomainLocal().copy(isDownloaded = true) 
            }
        }
    }

    override suspend fun deleteWorkoutPlan(planId: Int) {
        try { api.deleteWorkoutPlan(planId) } catch (_: Exception) { }
        workoutPlanDao.deleteWorkoutPlan(planId)
        workoutPlanDao.deleteWorkoutPlanCrossRefs(planId)
    }

    override suspend fun removeExerciseFromPlan(planId: Int, exerciseId: Int): WorkoutPlan {
        return api.removeExerciseFromPlan(planId, exerciseId).planToDomain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getDownloadedWorkoutPlans(): Flow<List<WorkoutPlan>> {
        return workoutPlanDao.getDownloadedWorkoutPlans().flatMapLatest { planEntities ->
            if (planEntities.isEmpty()) {
                flowOf(emptyList())
            } else {
                val plansFlows = planEntities.map { entity ->
                    workoutPlanDao.getExercisesForPlan(entity.id).map { exercises ->
                        entity.planToDomain().copy(
                            isDownloaded = true,
                            exercises = exercises.map { it.exerciseToDomainLocal().copy(isDownloaded = true) }
                        )
                    }
                }
                combine(plansFlows) { it.toList() }
            }
        }
    }

    override suspend fun getDownloadedPlanIds(): List<Int> {
        return workoutPlanDao.getDownloadedPlanIds()
    }

    override suspend fun removeLocalWorkoutPlanDownload(planId: Int) {
        workoutPlanDao.deleteWorkoutPlan(planId)
        workoutPlanDao.deleteWorkoutPlanCrossRefs(planId)
    }
}
