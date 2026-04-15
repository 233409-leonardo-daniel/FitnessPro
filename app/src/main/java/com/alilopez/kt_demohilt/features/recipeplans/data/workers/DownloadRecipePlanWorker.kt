package com.alilopez.kt_demohilt.features.recipeplans.data.workers

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.alilopez.kt_demohilt.core.database.dao.RecipeDao
import com.alilopez.kt_demohilt.core.database.dao.RecipePlanDao
import com.alilopez.kt_demohilt.core.database.entities.RecipeEntity
import com.alilopez.kt_demohilt.core.database.entities.RecipePlanEntity
import com.alilopez.kt_demohilt.core.database.entities.RecipePlanRecipeCrossRef
import com.alilopez.kt_demohilt.core.notifications.NotificationHelper
import com.alilopez.kt_demohilt.features.recipeplans.domain.repositories.RecipePlanRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import java.io.IOException

@HiltWorker
class DownloadRecipePlanWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: RecipePlanRepository,
    private val recipeDao: RecipeDao,
    private val recipePlanDao: RecipePlanDao
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_PLAN_ID = "plan_id"
        const val KEY_PLAN_NAME = "plan_name"
        const val KEY_PLAN_DESC = "plan_desc"
        const val KEY_USER_ID = "user_id"
    }

    override suspend fun doWork(): Result {
        val planId = inputData.getInt(KEY_PLAN_ID, -1)
        val planName = inputData.getString(KEY_PLAN_NAME) ?: "Plan"
        val planDesc = inputData.getString(KEY_PLAN_DESC) ?: ""
        val userId = inputData.getInt(KEY_USER_ID, -1)

        if (planId == -1 || userId == -1) return Result.failure()

        setForeground(createForegroundInfo(planName, 0))

        return try {
            val recipes = repository.getPlanRecipes(planId)
            val total = recipes.size

            recipePlanDao.insertRecipePlan(
                RecipePlanEntity(
                    id = planId,
                    name = planName,
                    description = planDesc,
                    userId = userId,
                    isPrivate = true,
                    isDownloaded = true
                )
            )

            recipes.forEachIndexed { index, recipe ->
                val progress = ((index + 1).toFloat() / total * 100).toInt()
                setForeground(createForegroundInfo(planName, progress))

                delay(300)

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

                recipePlanDao.insertRecipePlanCrossRef(
                    RecipePlanRecipeCrossRef(planId, recipe.id)
                )
            }

            Result.success()
        } catch (e: IOException) {
            // Error de red: indicamos a WorkManager que REINTENTE automáticamente
            // cuando las restricciones (WiFi) se vuelvan a cumplir.
            Log.e("DOWNLOAD_WORKER", "Error de red, reintentando... ${e.message}")
            Result.retry()
        } catch (e: Exception) {
            Log.e("DOWNLOAD_WORKER", "Error fatal: ${e.message}")
            Result.failure()
        }
    }

    private fun createForegroundInfo(planName: String, progress: Int): ForegroundInfo {
        val notification = NotificationCompat.Builder(context, NotificationHelper.DOWNLOAD_CHANNEL_ID)
            .setContentTitle("Descargando $planName")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setProgress(100, progress, false)
            .build()

        return ForegroundInfo(NotificationHelper.DOWNLOAD_NOTIFICATION_ID, notification)
    }
}
