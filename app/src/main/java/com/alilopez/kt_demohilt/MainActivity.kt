package com.alilopez.kt_demohilt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.alilopez.kt_demohilt.core.navigation.NavigationWrapper
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.core.ui.theme.AppTheme
import com.alilopez.kt_demohilt.features.recipeplans.domain.manager.MealReminderManager
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var mealReminderManager: MealReminderManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar AdMob al abrir la app.
        MobileAds.initialize(this)

        // Programar los recordatorios de comida
        mealReminderManager.scheduleDailyReminders()

        enableEdgeToEdge()
        setContent {
            AppTheme {
                NavigationWrapper(sessionManager = sessionManager)
            }
        }
    }
}
