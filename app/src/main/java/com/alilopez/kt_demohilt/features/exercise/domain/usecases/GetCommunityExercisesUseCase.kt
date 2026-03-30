package com.alilopez.kt_demohilt.features.exercise.domain.usecases

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.domain.repositories.ExerciseRepository
import javax.inject.Inject

class GetCommunityExercisesUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {

    suspend operator fun invoke(userId: Int): Result<List<Exercise>> {
        return try {
            val exercises = repository.getCommunityExercises(userId)
            val filteredExercises = exercises.filter { it.name.isNotBlank() }

            if (filteredExercises.isEmpty()) {
                Result.failure(Exception("No se encontraron ejercicios comunitarios válidos"))
            } else {
                Result.success(filteredExercises)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}