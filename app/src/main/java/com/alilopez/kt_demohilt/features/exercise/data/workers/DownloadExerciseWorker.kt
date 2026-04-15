package com.alilopez.kt_demohilt.features.exercise.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alilopez.kt_demohilt.core.notifications.NotificationHelper
import com.alilopez.kt_demohilt.features.exercise.domain.repositories.ExerciseRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class DownloadExerciseWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: ExerciseRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val exerciseId = inputData.getInt("exercise_id", -1)
        val exerciseName = inputData.getString("exercise_name") ?: "Ejercicio"

        if (exerciseId == -1) {
            Log.w("DOWNLOAD_WORKER", "Abortado: ID de ejercicio inválido")
            return Result.failure()
        }

        return try {
            Log.d("DOWNLOAD_WORKER", "Iniciando descarga para: $exerciseName ($exerciseId)")
            
            // Simular descarga de contenido pesado (imágenes/videos/assets)
            // Aquí iría la lógica real de descarga de media (si tuvieras descargas de archivos)
            // Por ahora, como es de texto/imagen básica, solo marcamos disponible offline.
            delay(2000) 
            
            // Persistir primero en Room y dejarlo marcado como offline.
            repository.saveExerciseForOffline(exerciseId)
            
            Log.d("DOWNLOAD_WORKER", "Descarga completada: $exerciseName")
            notificationHelper.showPaymentNotification( // Podemos reusar o crear una específica para descargas
                "Descarga Completada",
                "$exerciseName ahora está disponible sin conexión."
            )
            
            Result.success()
        } catch (e: Exception) {
            Log.e("DOWNLOAD_WORKER", "Error en la descarga: ${e.message}")
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
