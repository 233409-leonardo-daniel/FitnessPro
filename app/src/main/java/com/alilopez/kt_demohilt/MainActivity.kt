package com.alilopez.kt_demohilt

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.alilopez.kt_demohilt.core.navigation.NavigationWrapper
import com.alilopez.kt_demohilt.core.notifications.NotificationHelper
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.core.ui.theme.AppTheme
import com.alilopez.kt_demohilt.features.recipeplans.domain.manager.MealReminderManager
import com.alilopez.kt_demohilt.features.user.domain.usecases.UpdateFcmTokenUseCase
import com.alilopez.kt_demohilt.features.workoutplans.data.workers.PlanSyncWorker
import com.google.android.gms.ads.MobileAds
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var mealReminderManager: MealReminderManager

    @Inject
    lateinit var updateFcmTokenUseCase: UpdateFcmTokenUseCase

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("PERMISSIONS", "Permiso de notificaciones concedido")
        } else {
            Log.w("PERMISSIONS", "Permiso de notificaciones denegado")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar AdMob al abrir la app.
        MobileAds.initialize(this)

        // Pedir permisos de notificación en Android 13+
        askNotificationPermission()

        // Solo programar recordatorios y sincronización si está logueado
        if (sessionManager.isLoggedIn()) {
            mealReminderManager.scheduleDailyReminders()
            syncFcmToken()
            schedulePlanSync()
        }

        // Obtener ID de receta si viene de una notificación
        val initialRecipeId = intent.getIntExtra(NotificationHelper.EXTRA_RECIPE_ID, -1).takeIf { it != -1 }

        enableEdgeToEdge()
        setContent {
            AppTheme {
                NavigationWrapper(
                    sessionManager = sessionManager,
                    initialRecipeId = initialRecipeId
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Ejecutar sincronización inmediata al entrar/volver a la app (Punto solicitado)
        if (sessionManager.isLoggedIn()) {
            val syncRequest = OneTimeWorkRequestBuilder<PlanSyncWorker>()
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
            WorkManager.getInstance(this).enqueueUniqueWork(
                "PlanSyncImmediate",
                ExistingWorkPolicy.REPLACE,
                syncRequest
            )
        }
    }

    private fun schedulePlanSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicSyncRequest = PeriodicWorkRequestBuilder<PlanSyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "PlanSyncPeriodic",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncRequest
        )
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val recipeId = intent.getIntExtra(NotificationHelper.EXTRA_RECIPE_ID, -1).takeIf { it != -1 }
        if (recipeId != null) {
            setIntent(intent)
            recreate()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun syncFcmToken() {
        val userId = sessionManager.currentUserId
        if (userId != null) {
            lifecycleScope.launch {
                try {
                    val token = FirebaseMessaging.getInstance().token.await()
                    updateFcmTokenUseCase(userId, token)
                    Log.d("FCM", "Token sincronizado en el arranque para usuario $userId")
                } catch (e: Exception) {
                    Log.e("FCM", "Error sincronizando token en el arranque: ${e.message}")
                }
            }
        }
    }
}
