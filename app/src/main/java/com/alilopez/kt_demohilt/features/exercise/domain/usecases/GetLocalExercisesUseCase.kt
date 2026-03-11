package com.alilopez.kt_demohilt.features.exercise.domain.usecases

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.domain.repositories.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocalExercisesUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {

     operator fun invoke(): Flow<List<Exercise>> {
        return repository.getLocalExercises()
    }
}

