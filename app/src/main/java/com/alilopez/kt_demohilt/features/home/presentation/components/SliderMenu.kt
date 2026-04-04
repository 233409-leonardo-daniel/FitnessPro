package com.alilopez.kt_demohilt.features.home.presentation.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SliderMenu(
    onNavigateToHome: () -> Unit,
    onNavigateToRecipes: () -> Unit,
    onNavigateToExercises: () -> Unit,
    onNavigateToWorkoutPlans: () -> Unit,
    onNavigateToRecipePlans: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onLogout: () -> Unit,
    isPremium: Boolean = false,
    currentRoute: String? = null,
    onCloseDrawer: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val accentColor = Color(0xFF10B981)
    val premiumColor = Color(0xFFF59E0B)

    ModalDrawerSheet(
        drawerContainerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White,
        modifier = Modifier.width(280.dp)
    ) {
        // Eliminado Spacer(Modifier.height(24.dp)) para evitar espacio doble arriba
        Text(
            "FitnessPro",
            modifier = Modifier.padding(16.dp),
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium,
            color = accentColor
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        DrawerItem(
            label = "Inicio",
            icon = Icons.Default.Home,
            selected = currentRoute == "Home",
            onClick = {
                onNavigateToHome()
                onCloseDrawer()
            },
            accentColor = accentColor,
            textColor = textColor
        )

        DrawerItem(
            label = "Recetas",
            icon = Icons.Default.Restaurant,
            selected = currentRoute == "Recipes",
            onClick = {
                onNavigateToRecipes()
                onCloseDrawer()
            },
            accentColor = accentColor,
            textColor = textColor
        )

        DrawerItem(
            label = "Ejercicios",
            icon = Icons.Default.FitnessCenter,
            selected = currentRoute == "Exercises",
            onClick = {
                onNavigateToExercises()
                onCloseDrawer()
            },
            accentColor = accentColor,
            textColor = textColor
        )

        DrawerItem(
            label = "Mis Rutinas",
            icon = Icons.AutoMirrored.Filled.ListAlt,
            selected = currentRoute == "WorkoutPlans",
            onClick = {
                onNavigateToWorkoutPlans()
                onCloseDrawer()
            },
            accentColor = accentColor,
            textColor = textColor
        )

        DrawerItem(
            label = "Mis Menús",
            icon = Icons.Default.MenuBook,
            selected = currentRoute == "RecipePlans",
            onClick = {
                onNavigateToRecipePlans()
                onCloseDrawer()
            },
            accentColor = accentColor,
            textColor = textColor
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        DrawerItem(
            label = "Mi Perfil",
            icon = Icons.Default.Person,
            selected = currentRoute == "Profile",
            onClick = {
                onNavigateToProfile()
                onCloseDrawer()
            },
            accentColor = accentColor,
            textColor = textColor
        )

        if (!isPremium) {
            DrawerItem(
                label = "Hazte Premium",
                icon = Icons.Default.Stars,
                selected = currentRoute == "Premium",
                onClick = {
                    onNavigateToPremium()
                    onCloseDrawer()
                },
                accentColor = premiumColor,
                textColor = premiumColor
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        DrawerItem(
            label = "Cerrar sesión",
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            selected = false,
            onClick = {
                onLogout()
                onCloseDrawer()
            },
            accentColor = Color(0xFFEF4444),
            textColor = Color(0xFFEF4444)
        )
    }
}

@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    accentColor: Color,
    textColor: Color
) {
    NavigationDrawerItem(
        label = { Text(label, color = if (selected) accentColor else textColor) },
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null, tint = if (selected) accentColor else textColor) },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = accentColor.copy(alpha = 0.1f),
            unselectedContainerColor = Color.Transparent
        )
    )
}
