package com.alilopez.kt_demohilt.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

interface FeatureNavGraph {
    fun registerNavGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController
    )
}
