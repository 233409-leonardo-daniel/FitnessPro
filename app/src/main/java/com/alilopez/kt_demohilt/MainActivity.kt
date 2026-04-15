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
import com.alilopez.kt_demohilt.core.navigation.NavigationWrapper
import com.alilopez.kt_demohilt.core.notifications.NotificationHelper
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.core.ui.theme.AppTheme
import com.alilopez.kt_demohilt.features.recipeplans.domain.manager.MealReminderManager
import com.alilopez.kt_demohilt.features.user.domain.usecases.UpdateFcmTokenUseCase
import com.google.android.gms.ads.MobileAds
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

        // Solo programar recordatorios si está logueado (Punto 6)
        if (sessionManager.isLoggedIn()) {
            mealReminderManager.scheduleDailyReminders()
            syncFcmToken()
        }

        // Obtener ID de receta si viene de una notificación (Punto 7)
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Manejar el clic en la notificación si la app ya estaba abierta
        val recipeId = intent.getIntExtra(NotificationHelper.EXTRA_RECIPE_ID, -1).takeIf { it != -1 }
        if (recipeId != null) {
            // Aquí se podría usar un StateFlow en un ViewModel compartido para navegar
            // Por ahora, el relanzamiento de la actividad con el nuevo intent activará el flujo
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
