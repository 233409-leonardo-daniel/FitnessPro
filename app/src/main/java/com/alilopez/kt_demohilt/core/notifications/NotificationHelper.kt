package com.alilopez.kt_demohilt.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.alilopez.kt_demohilt.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    private val context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val PAYMENT_CHANNEL_ID = "payment_channel"
        const val MEAL_CHANNEL_ID = "meal_channel"
        const val PAYMENT_NOTIFICATION_ID = 1001
        const val MEAL_NOTIFICATION_ID = 1002
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

            notificationManager.createNotificationChannel(paymentChannel)
            notificationManager.createNotificationChannel(mealChannel)
        }
    }

    fun showPaymentNotification(title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, PAYMENT_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Cambiar por icono de la app luego
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(PAYMENT_NOTIFICATION_ID, notification)
    }

    fun showMealNotification(title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 1, intent,
            PendingIntent.FLAG_IMMUTABLE
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
}
