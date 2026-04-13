package com.alilopez.kt_demohilt.features.recipeplans.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alilopez.kt_demohilt.core.notifications.NotificationHelper
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.features.recipies.domain.repositories.RecipeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@HiltWorker
class MealReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: RecipeRepository,
    private val sessionManager: SessionManager,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val userId = sessionManager.currentUserId 
        if (userId == null) {
            Log.w("MEAL_WORKER", "Abortado: No hay usuario logueado")
            return Result.failure()
        }
        
        return try {
            Log.d("MEAL_WORKER", "Buscando recetas para usuario: $userId")
            val recipes = repository.getUserRecipes(userId)
            
            val calendar = Calendar.getInstance()
            
            // 1. Obtener el día actual en español (Lunes, Martes, etc.)
            val dayFormat = SimpleDateFormat("EEEE", Locale("es", "ES"))
            val currentDayName = dayFormat.format(calendar.time).replaceFirstChar { it.uppercase() }
            
            // 2. Determinar el tipo de comida según la hora (Rangos normales)
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val mealType = when (currentHour) {
                in 6..11 -> "DESAYUNO"
                in 12..16 -> "COMIDA"
                in 19..23 -> "CENA"
                else -> null
            }

            Log.d("MEAL_WORKER", "Hoy es: $currentDayName | Hora: $currentHour | Tipo: $mealType")

            if (mealType != null) {
                // 3. Filtrar por TIPO y por DÍA
                val recipeForToday = recipes.find { recipe ->
                    val matchesType = recipe.mealType?.uppercase() == mealType
                    // Verificamos si el nombre del día actual está en la lista de días programados
                    val matchesDay = recipe.scheduledDays?.any { 
                        it.trim().equals(currentDayName, ignoreCase = true) 
                    } ?: false
                    
                    matchesType && matchesDay
                }

                if (recipeForToday != null) {
                    notificationHelper.showMealNotification(
                        "¡Es hora de tu $mealType!",
                        "Hoy te toca preparar: ${recipeForToday.name}"
                    )
                    Log.d("MEAL_WORKER", "Notificación enviada: ${recipeForToday.name}")
                } else {
                    Log.d("MEAL_WORKER", "No hay recetas para $mealType programadas para hoy ($currentDayName).")
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            Log.e("MEAL_WORKER", "Error en el Worker: ${e.message}")
            Result.retry()
        }
    }
}
