package com.alilopez.kt_demohilt.features.exercise.domain.usecases

import com.alilopez.kt_demohilt.features.exercise.domain.repositories.ExerciseRepository
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import javax.inject.Inject

class GetExercisesByBodyPartUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {

    suspend operator fun invoke(bodyPart: String): Result<List<Exercise>> {
        return try {
            if (bodyPart.isBlank()) {
                return Result.failure(Exception("El bodyPart no puede estar vacío"))
            }

            val exercises = repository.getExercisesByBodyPart(bodyPart)
            val filteredExercises = exercises.filter { it.name.isNotBlank() }

            if (filteredExercises.isEmpty()) {
                Result.failure(Exception("No se encontraron ejercicios para '$bodyPart'"))
            } else {
                Result.success(filteredExercises)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
