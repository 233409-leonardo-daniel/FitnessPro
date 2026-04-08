package com.alilopez.kt_demohilt.features.recipeplans.domain.manager

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
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

        // Ejecutar cada 4 horas para revisar si toca alguna comida
        val mealWorkRequest = PeriodicWorkRequestBuilder<MealReminderWorker>(
            4, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .addTag("meal_reminder")
            .build()

        workManager.enqueueUniquePeriodicWork(
            MEAL_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Mantiene la existente si ya está programada
            mealWorkRequest
        )
    }

    fun cancelReminders() {
        workManager.cancelUniqueWork(MEAL_REMINDER_WORK_NAME)
    }
}
