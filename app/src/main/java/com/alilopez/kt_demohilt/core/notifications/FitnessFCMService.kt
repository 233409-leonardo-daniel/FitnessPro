package com.alilopez.kt_demohilt.core.notifications

import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.alilopez.kt_demohilt.core.network.FitnessProApi
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.features.user.data.workers.SyncSubscriptionWorker
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FitnessFCMService : FirebaseMessagingService() {

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var api: FitnessProApi

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var notificationHelper: NotificationHelper

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        if (remoteMessage.data.isNotEmpty()) {
            val type = remoteMessage.data["type"]
            
            when (type) {
                "PAYMENT_SUCCESS" -> {
                    val subId = remoteMessage.data["subscription_id"]?.toIntOrNull()
                    if (subId != null) triggerSyncWorker(subId)
                }
                "NEW_CONTENT" -> {
                    val title = remoteMessage.data["title"] ?: "¡Nuevo contenido!"
                    val message = remoteMessage.data["message"] ?: "Descubre lo nuevo en FitnessPro"
                    notificationHelper.showContentNotification(title, message)
                }
            }
        }
        
        // Si el servidor envía una notificación visual estándar
        remoteMessage.notification?.let {
            notificationHelper.showContentNotification(it.title ?: "", it.body ?: "")
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
        sessionManager.currentUserId?.let { userId ->
            scope.launch {
                try {
                    api.updateFcmToken(userId, mapOf("fcm_token" to token))
                } catch (_: Exception) { }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
