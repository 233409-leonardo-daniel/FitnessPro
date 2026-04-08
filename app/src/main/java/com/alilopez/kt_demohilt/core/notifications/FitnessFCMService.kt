package com.alilopez.kt_demohilt.core.notifications

import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.alilopez.kt_demohilt.features.user.data.workers.SyncSubscriptionWorker
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FitnessFCMService : FirebaseMessagingService() {

    @Inject
    lateinit var workManager: WorkManager

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Verificamos si el mensaje tiene datos
        if (remoteMessage.data.isNotEmpty()) {
            val type = remoteMessage.data["type"]
            val subId = remoteMessage.data["subscription_id"]?.toIntOrNull()

            if (type == "PAYMENT_SUCCESS" && subId != null) {
                triggerSyncWorker(subId)
            }
        }
    }

    private fun triggerSyncWorker(subscriptionId: Int) {
        val data = Data.Builder()
            .putInt("subscription_id", subscriptionId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<SyncSubscriptionWorker>()
            .setInputData(data)
            .build()

        workManager.enqueue(workRequest)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Aquí deberías enviar el token a tu API para guardarlo
        // api.updateUserFcmToken(token)
    }
}
