package com.alilopez.kt_demohilt.features.exercise.domain.usecases

import com.alilopez.kt_demohilt.features.exercise.domain.repositories.ExerciseRepository
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import javax.inject.Inject

class GetExercisesUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {

    suspend operator fun invoke(): Result<List<Exercise>> {
        return try {
            val exercises = repository.getExercises()
            val filteredExercises = exercises.filter { it.name.isNotBlank() }

            if (filteredExercises.isEmpty()) {
                Result.failure(Exception("No se encontraron ejercicios válidos"))
            } else {
                Result.success(filteredExercises)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
