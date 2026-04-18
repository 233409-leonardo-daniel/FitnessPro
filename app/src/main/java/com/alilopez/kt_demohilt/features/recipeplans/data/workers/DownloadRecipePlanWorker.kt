package com.alilopez.kt_demohilt.features.recipeplans.data.workers

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
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
    private val recipePlanDao: RecipePlanDao,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_PLAN_ID = "plan_id"
        const val KEY_PLAN_NAME = "plan_name"
        const val KEY_PLAN_DESC = "plan_desc"
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
        val planName = inputData.getString(KEY_PLAN_NAME) ?: "Plan"
        val planDesc = inputData.getString(KEY_PLAN_DESC) ?: ""
        val userId = inputData.getInt(KEY_USER_ID, -1)

        if (planId == -1 || userId == -1) return Result.failure()

        setForeground(buildForegroundInfo(planName, 0))

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
                
                setForeground(buildForegroundInfo(planName, progress))

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

                recipePlanDao.insertRecipePlanRecipeCrossRef(
                    RecipePlanRecipeCrossRef(planId, recipe.id)
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
