package com.alilopez.kt_demohilt

import android.Manifest
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
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.core.ui.theme.AppTheme
import com.alilopez.kt_demohilt.features.exercise.domain.usecases.ClearAllOfflineExercisesUseCase
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

    @Inject
    lateinit var clearAllOfflineExercisesUseCase: ClearAllOfflineExercisesUseCase

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

        // Programar los recordatorios de comida
        mealReminderManager.scheduleDailyReminders()

        // En MainActivity.kt
        mealReminderManager.runTestNow()

        // Asegurar que el token de FCM esté registrado si ya hay sesión
        syncFcmToken()

        // Observar cambios en la membresía para limpiar descargas offline
        observeMembershipChanges()

        enableEdgeToEdge()
        setContent {
            AppTheme {
                NavigationWrapper(sessionManager = sessionManager)
            }
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

    private fun observeMembershipChanges() {
        lifecycleScope.launch {
            sessionManager.membership.collect { membership ->
                if (sessionManager.isFreeMembership(membership)) {
                    try {
                        clearAllOfflineExercisesUseCase()
                        Log.d("OfflineExercises", "Se eliminaron todas las descargas porque la membresia cambió a gratuito")
                    } catch (e: Exception) {
                        Log.e("OfflineExercises", "Error al limpiar descargas: ${e.message}")
                    }
                }
            }
        }
    }
}
