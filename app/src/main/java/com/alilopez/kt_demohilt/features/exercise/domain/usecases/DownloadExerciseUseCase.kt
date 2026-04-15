package com.alilopez.kt_demohilt.features.exercise.domain.usecases

import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.alilopez.kt_demohilt.features.exercise.data.workers.DownloadExerciseWorker
import javax.inject.Inject

class DownloadExerciseUseCase @Inject constructor(
    private val workManager: WorkManager
) {
    operator fun invoke(exerciseId: Int, exerciseName: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true) // No con poca batería
            .build()

        val inputData = Data.Builder()
            .putInt("exercise_id", exerciseId)
            .putString("exercise_name", exerciseName)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<DownloadExerciseWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        workManager.enqueue(workRequest)
    }
}