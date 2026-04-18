package com.alilopez.kt_demohilt.features.workoutplans.data.workers

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.alilopez.kt_demohilt.core.database.dao.ExerciseDao
import com.alilopez.kt_demohilt.core.database.dao.WorkoutPlanDao
import com.alilopez.kt_demohilt.core.database.entities.ExerciseEntity
import com.alilopez.kt_demohilt.core.database.entities.WorkoutPlanEntity
import com.alilopez.kt_demohilt.core.database.entities.WorkoutPlanExerciseCrossRef
import com.alilopez.kt_demohilt.core.notifications.NotificationHelper
import com.alilopez.kt_demohilt.features.workoutplans.domain.repositories.WorkoutPlanRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import java.io.IOException

@HiltWorker
class DownloadWorkoutPlanWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: WorkoutPlanRepository,
    private val exerciseDao: ExerciseDao,
    private val workoutPlanDao: WorkoutPlanDao,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_PLAN_ID = "plan_id"
        const val KEY_PLAN_NAME = "plan_name"
        const val KEY_PLAN_DESC = "plan_desc"
        const val KEY_PLAN_TYPE = "plan_type"
        const val KEY_USER_ID = "user_id"
    }

    override suspend fun getForegroundInfo(): ForegroundInfo =
        buildForegroundInfo(inputData.getString(KEY_PLAN_NAME) ?: "Plan", 0)

    private fun buildForegroundInfo(planName: String, progress: Int): ForegroundInfo {
        val notification = notificationHelper.getDownloadNotification(planName, progress)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NotificationHelper.DOWNLOAD_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(NotificationHelper.DOWNLOAD_NOTIFICATION_ID, notification)
        }
    }

    override suspend fun doWork(): Result {
        val planId = inputData.getInt(KEY_PLAN_ID, -1)
        val planName = inputData.getString(KEY_PLAN_NAME) ?: "Plan de Entrenamiento"
        val planDesc = inputData.getString(KEY_PLAN_DESC) ?: ""
        val planType = inputData.getString(KEY_PLAN_TYPE) ?: ""
        val userId = inputData.getInt(KEY_USER_ID, -1)

        if (planId == -1 || userId == -1) return Result.failure()

        setForeground(buildForegroundInfo(planName, 0))

        return try {
            val exercises = repository.getPlanExercises(planId)
            val total = exercises.size

            workoutPlanDao.insertWorkoutPlan(
                WorkoutPlanEntity(
                    id = planId,
                    name = planName,
                    description = planDesc,
                    userId = userId,
                    planType = planType,
                    isPrivate = true,
                    isDownloaded = true
                )
            )

            exercises.forEachIndexed { index, exercise ->
                val progress = ((index + 1).toFloat() / total * 100).toInt()
                
                setForeground(buildForegroundInfo(planName, progress))

                delay(300)

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
                    WorkoutPlanExerciseCrossRef(
                        planId = planId,
                        exerciseId = exercise.id ?: 0
                    )
                )
            }

            Result.success()
        } catch (e: IOException) {
            Log.e("DOWNLOAD_WORKER", "Error de red, reintentando...")
            Result.retry()
        } catch (e: Exception) {
            Log.e("DOWNLOAD_WORKER", "Error fatal: ${e.message}")
            Result.failure()
        }
    }
}
