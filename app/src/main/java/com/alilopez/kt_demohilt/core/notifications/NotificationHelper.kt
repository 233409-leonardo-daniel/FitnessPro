package com.alilopez.kt_demohilt.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.alilopez.kt_demohilt.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val PAYMENT_CHANNEL_ID = "payment_channel"
        const val MEAL_CHANNEL_ID = "meal_channel"
        const val CONTENT_CHANNEL_ID = "content_channel"
        const val DOWNLOAD_CHANNEL_ID = "download_channel"
        
        const val PAYMENT_NOTIFICATION_ID = 1001
        const val MEAL_NOTIFICATION_ID = 1002
        const val CONTENT_NOTIFICATION_ID = 1003
        const val DOWNLOAD_NOTIFICATION_ID = 2001

        const val EXTRA_RECIPE_ID = "extra_recipe_id"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val paymentChannel = NotificationChannel(
                PAYMENT_CHANNEL_ID,
                "Pagos y Suscripciones",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones sobre el estado de tus pagos"
            }

            val mealChannel = NotificationChannel(
                MEAL_CHANNEL_ID,
                "Recordatorios de Comidas",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Avisos para tus desayunos, comidas y cenas"
            }

            val contentChannel = NotificationChannel(
                CONTENT_CHANNEL_ID,
                "Novedades de la Comunidad",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Avisos sobre nuevas recetas y ejercicios"
            }

            val downloadChannel = NotificationChannel(
                DOWNLOAD_CHANNEL_ID,
                "Descargas Offline",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Progreso de descargas de planes"
            }

            notificationManager.createNotificationChannel(paymentChannel)
            notificationManager.createNotificationChannel(mealChannel)
            notificationManager.createNotificationChannel(contentChannel)
            notificationManager.createNotificationChannel(downloadChannel)
        }
    }

    fun showPaymentNotification(title: String, message: String) {
        showBasicNotification(title, message, PAYMENT_CHANNEL_ID, PAYMENT_NOTIFICATION_ID)
    }

    fun showMealNotification(title: String, message: String, recipeId: Int? = null) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (recipeId != null) {
                putExtra(EXTRA_RECIPE_ID, recipeId)
            }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, MEAL_NOTIFICATION_ID, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, MEAL_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(MEAL_NOTIFICATION_ID, notification)
    }

    fun showContentNotification(title: String, message: String) {
        showBasicNotification(title, message, CONTENT_CHANNEL_ID, CONTENT_NOTIFICATION_ID)
    }

    private fun showBasicNotification(title: String, message: String, channelId: String, notificationId: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
