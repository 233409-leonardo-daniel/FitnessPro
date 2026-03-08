package com.alilopez.kt_demohilt.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun NavigationWrapper(navcGraphs: List<FeatureNavGraph>) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Login) {
        navcGraphs.forEach { graph ->
            graph.registerNavGraph(this, navController)
        }
    }
}
