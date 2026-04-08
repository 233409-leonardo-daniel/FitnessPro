package com.alilopez.kt_demohilt.features.recipeplans.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alilopez.kt_demohilt.core.notifications.NotificationHelper
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.features.recipeplans.domain.repositories.RecipePlanRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar

@HiltWorker
class MealReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: RecipePlanRepository,
    private val sessionManager: SessionManager,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val userId = sessionManager.currentUserId ?: return Result.failure()
        
        return try {
            val plans = repository.getUserRecipePlans(userId)
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            
            // Lógica simple para determinar qué tipo de comida recordar según la hora
            val mealType = when (currentHour) {
                in 6..10 -> "DESAYUNO"
                in 12..15 -> "COMIDA"
                in 19..22 -> "CENA"
                else -> null
            }

            if (mealType != null) {
                // Buscamos si hay alguna receta en los planes para este tipo de comida
                val recipeForToday = plans.flatMap { it.recipes }
                    .find { it.mealType?.uppercase() == mealType }

                if (recipeForToday != null) {
                    notificationHelper.showMealNotification(
                        "¡Hora de tu $mealType!",
                        "Hoy te toca: ${recipeForToday.name}"
                    )
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
