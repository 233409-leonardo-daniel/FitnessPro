package com.alilopez.kt_demohilt.features.workoutplans.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alilopez.kt_demohilt.core.database.dao.ExerciseDao
import com.alilopez.kt_demohilt.core.database.dao.RecipeDao
import com.alilopez.kt_demohilt.core.database.dao.RecipePlanDao
import com.alilopez.kt_demohilt.core.database.dao.WorkoutPlanDao
import com.alilopez.kt_demohilt.core.database.entities.ExerciseEntity
import com.alilopez.kt_demohilt.core.database.entities.RecipeEntity
import com.alilopez.kt_demohilt.core.database.entities.RecipePlanRecipeCrossRef
import com.alilopez.kt_demohilt.core.database.entities.WorkoutPlanExerciseCrossRef
import com.alilopez.kt_demohilt.features.recipeplans.domain.repositories.RecipePlanRepository
import com.alilopez.kt_demohilt.features.workoutplans.domain.repositories.WorkoutPlanRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

@HiltWorker
class PlanSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val workoutPlanRepository: WorkoutPlanRepository,
    private val recipePlanRepository: RecipePlanRepository,
    private val workoutPlanDao: WorkoutPlanDao,
    private val recipePlanDao: RecipePlanDao,
    private val exerciseDao: ExerciseDao,
    private val recipeDao: RecipeDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = coroutineScope {
        Log.d("PlanSyncWorker", ">>> [SYNC START] Iniciando sincronización de planes en segundo plano...")
        try {
            val workoutJob = async { syncWorkoutPlans() }
            val recipeJob = async { syncRecipePlans() }
            
            awaitAll(workoutJob, recipeJob)
            
            Log.d("PlanSyncWorker", ">>> [SYNC SUCCESS] Sincronización completada con éxito")
            Result.success()
        } catch (e: Exception) {
            Log.e("PlanSyncWorker", ">>> [SYNC ERROR] Error en la sincronización: ${e.message}")
            Result.retry()
        }
    }

    private suspend fun syncWorkoutPlans() {
        val downloadedPlanIds = workoutPlanDao.getDownloadedPlanIds()
        Log.d("PlanSyncWorker", "Sincronizando ${downloadedPlanIds.size} planes de rutinas descargados")
        downloadedPlanIds.forEach { planId ->
            val remoteExercises = workoutPlanRepository.getPlanExercises(planId)
            val localExerciseIds = workoutPlanDao.getExerciseIdsForPlan(planId).toSet()

            remoteExercises.forEach { exercise ->
                if (!localExerciseIds.contains(exercise.id ?: 0)) {
                    Log.d("PlanSyncWorker", "Detectado nuevo ejercicio: ${exercise.name} para plan ID $planId")
                    exerciseDao.insertExercises(listOf(
                        ExerciseEntity(
                            id = exercise.id ?: 0,
                            name = exercise.name,
                            description = exercise.description ?: "",
                            user_id = exercise.userId ?: 0,
                            scheduled_days = exercise.scheduledDays.joinToString(","),
                            image_url = exercise.gifUrl,
                            bodyparts = exercise.bodyparts.joinToString(","),
                            equipments = exercise.equipments.joinToString(","),
                            targetMuscles = exercise.targetMuscles.joinToString(","),
                            secondaryMuscles = exercise.secondaryMuscles.joinToString(","),
                            exercise_type = exercise.exerciseType ?: "",
                            instruccions = exercise.instructions.joinToString("\n"),
                            difficulty = exercise.difficulty ?: "NORMAL",
                            private = exercise.private,
                            isDownloaded = true
                        )
                    ))
                    workoutPlanDao.insertWorkoutPlanExerciseCrossRef(
                        WorkoutPlanExerciseCrossRef(planId, exercise.id ?: 0)
                    )
                }
            }
        }
    }

    private suspend fun syncRecipePlans() {
        val downloadedPlanIds = recipePlanDao.getDownloadedPlanIds()
        Log.d("PlanSyncWorker", "Sincronizando ${downloadedPlanIds.size} listas de recetas descargadas")
        downloadedPlanIds.forEach { planId ->
            val remoteRecipes = recipePlanRepository.getPlanRecipes(planId)
            val localRecipeIds = recipePlanDao.getRecipeIdsForPlan(planId).toSet()

            remoteRecipes.forEach { recipe ->
                if (!localRecipeIds.contains(recipe.id)) {
                    Log.d("PlanSyncWorker", "Detectada nueva receta: ${recipe.name} para lista ID $planId")
                    recipeDao.insertRecipes(listOf(
                        RecipeEntity(
                            id = recipe.id,
                            name = recipe.name,
                            instructions = recipe.instructions,
                            ingredients = recipe.ingredients.split(","),
                            measures = emptyList(),
                            imageUrl = recipe.imageUrl ?: "",
                            category = "",
                            area = "",
                            tags = emptyList(),
                            youtubeUrl = "",
                            sourceUrl = "",
                            isDownloaded = true
                        )
                    ))
                    recipePlanDao.insertRecipePlanRecipeCrossRef(
                        RecipePlanRecipeCrossRef(planId, recipe.id)
                    )
                }
            }
        }
    }
}
