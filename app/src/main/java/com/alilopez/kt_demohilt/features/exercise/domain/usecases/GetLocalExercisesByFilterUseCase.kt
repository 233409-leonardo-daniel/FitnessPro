package com.alilopez.kt_demohilt.features.exercise.domain.usecases

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.domain.entities.ExerciseFilter
import com.alilopez.kt_demohilt.features.exercise.domain.repositories.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocalExercisesByFilterUseCase @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) {
    operator fun invoke(filter: ExerciseFilter): Flow<List<Exercise>> {
        return exerciseRepository.getLocalExercisesByFilter(filter)
    }
}