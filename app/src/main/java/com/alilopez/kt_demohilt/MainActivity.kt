package com.alilopez.kt_demohilt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.alilopez.kt_demohilt.core.navigation.NavigationWrapper
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.core.ui.theme.AppTheme
import com.startapp.sdk.adsbase.StartAppSDK
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar Start.io SDK con el App ID proporcionado
        StartAppSDK.init(this, "202383284", true)
        // Opcional: Desactivar el splash de Start.io si no se desea
        StartAppSDK.enableReturnAds(false)

        enableEdgeToEdge()
        setContent {
            AppTheme {
                NavigationWrapper(sessionManager = sessionManager)
            }
        }
    }
}
