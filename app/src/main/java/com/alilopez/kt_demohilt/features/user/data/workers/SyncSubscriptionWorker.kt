package com.alilopez.kt_demohilt.features.user.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alilopez.kt_demohilt.core.notifications.NotificationHelper
import com.alilopez.kt_demohilt.features.user.domain.repositories.SubscriptionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncSubscriptionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: SubscriptionRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val subscriptionId = inputData.getInt("subscription_id", -1)
        Log.d("SYNC_WORKER", "Iniciando sincronización para ID: $subscriptionId")
        
        if (subscriptionId == -1) return Result.failure()

        return try {
            val status = repository.getSubscriptionStatus(subscriptionId)
            Log.d("SYNC_WORKER", "Estado obtenido del servidor: $status")
            
            // Aquí la notificación se muestra SOLO si el Worker termina con éxito
            notificationHelper.showPaymentNotification(
                "¡Pago Confirmado!",
                "Tu suscripción ahora está activa. ¡Disfruta de FitnessPro!"
            )
            Result.success()
        } catch (e: Exception) {
            Log.e("SYNC_WORKER", "Error en la sincronización: ${e.message}")
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
