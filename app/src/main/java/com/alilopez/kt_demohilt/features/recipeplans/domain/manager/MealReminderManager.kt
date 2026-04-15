package com.alilopez.kt_demohilt.features.recipeplans.domain.manager

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.alilopez.kt_demohilt.features.recipeplans.data.workers.MealReminderWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealReminderManager @Inject constructor(
    private val workManager: WorkManager
) {
    companion object {
        private const val MEAL_REMINDER_WORK_NAME = "meal_reminder_periodic_work"
    }

    fun scheduleDailyReminders() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Ejecutar cada 15 minutos para la prueba (mínimo permitido por Android)
        val mealWorkRequest = PeriodicWorkRequestBuilder<MealReminderWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag("meal_reminder")
            .build()

        workManager.enqueueUniquePeriodicWork(
            MEAL_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE, // Forzamos la actualización para que tome los cambios del Worker
            mealWorkRequest
        )
    }

    // NUEVA FUNCIÓN: Dispara la notificación AL INSTANTE para probar ahora mismo
    fun runTestNow() {
        val testRequest = OneTimeWorkRequestBuilder<MealReminderWorker>()
            .addTag("meal_test_now")
            .build()
        workManager.enqueue(testRequest)
    }

    fun cancelReminders() {
        workManager.cancelUniqueWork(MEAL_REMINDER_WORK_NAME)
    }
}
