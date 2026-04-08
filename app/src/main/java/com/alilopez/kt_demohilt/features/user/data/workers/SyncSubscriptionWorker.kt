package com.alilopez.kt_demohilt.features.user.data.workers

import android.content.Context
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
        if (subscriptionId == -1) return Result.failure()

        return try {
            val status = repository.getSubscriptionStatus(subscriptionId)
            // Aquí podrías guardar el status en Room si tuvieras una DB local para el usuario
            
            notificationHelper.showPaymentNotification(
                "¡Pago Confirmado!",
                "Tu suscripción ahora está activa. ¡Disfruta de FitnessPro!"
            )
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
