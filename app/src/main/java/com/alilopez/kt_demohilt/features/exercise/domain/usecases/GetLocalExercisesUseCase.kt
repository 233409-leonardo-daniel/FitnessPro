package com.alilopez.kt_demohilt.features.exercise.domain.usecases

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.domain.repositories.ExerciseRepository
import javax.inject.Inject

class GetLocalExercisesUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {

    suspend operator fun invoke(): Result<List<Exercise>> {
        return try {
            val exercises = repository.getLocalExercises()
            Result.success(exercises)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

