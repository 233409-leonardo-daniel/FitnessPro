package com.alilopez.kt_demohilt.features.exercise.domain.usecases

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.domain.repositories.ExerciseRepository
import javax.inject.Inject

class GetExercisesByUserIdUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {
    suspend operator fun invoke(userId: Int): Result<List<Exercise>> {
        return try {
            if (userId <= 0) {
                return Result.failure(Exception("El userId no es valido"))
            }

            val exercises = repository.getExercisesByUserId(userId)
            Result.success(exercises.filter { it.name.isNotBlank() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

