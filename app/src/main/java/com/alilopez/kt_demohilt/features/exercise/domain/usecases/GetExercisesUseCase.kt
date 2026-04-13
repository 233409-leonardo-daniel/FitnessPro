package com.alilopez.kt_demohilt.features.exercise.domain.usecases

import com.alilopez.kt_demohilt.features.exercise.domain.entities.PaginatedExercises
import com.alilopez.kt_demohilt.features.exercise.domain.repositories.ExerciseRepository
import javax.inject.Inject

class GetExercisesUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {

    suspend operator fun invoke(
        offset: Int? = null,
        limit: Int = 25
    ): Result<PaginatedExercises> {
        return try {
            val result = repository.getRemoteExercises(
                limit = limit,
                offset = offset
            )
            val filteredExercises = result.exercises.filter { it.name.isNotBlank() }

            if (filteredExercises.isEmpty() && offset == null) {
                Result.failure(Exception("No se encontraron ejercicios válidos"))
            } else {
                Result.success(
                    result.copy(exercises = filteredExercises)
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
