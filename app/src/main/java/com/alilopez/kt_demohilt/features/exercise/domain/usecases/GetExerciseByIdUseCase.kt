package com.alilopez.kt_demohilt.features.exercise.domain.usecases

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.domain.repositories.ExerciseRepository
import javax.inject.Inject

class GetExerciseByIdUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {
    suspend operator fun invoke(exerciseId: Int): Result<Exercise> = try {
        Result.success(repository.getExerciseById(exerciseId))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
