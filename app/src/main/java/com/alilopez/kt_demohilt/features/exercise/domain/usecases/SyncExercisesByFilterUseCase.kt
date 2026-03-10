package com.alilopez.kt_demohilt.features.exercise.domain.usecases

import com.alilopez.kt_demohilt.features.exercise.domain.entities.ExerciseFilter
import com.alilopez.kt_demohilt.features.exercise.domain.repositories.ExerciseRepository
import javax.inject.Inject

class SyncExercisesByFilterUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {
    suspend operator fun invoke(filter: ExerciseFilter) {
        repository.syncExercisesByFilter(filter)
    }
}

