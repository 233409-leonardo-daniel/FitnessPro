package com.alilopez.kt_demohilt.features.exercise.domain.usecases

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.domain.repositories.ExerciseRepository
import java.io.File
import javax.inject.Inject

class UpdateLocalExerciseUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {
    suspend operator fun invoke(
        exerciseId: Int,
        name: String,
        description: String,
        scheduledDays: List<String> = emptyList(),
        bodyparts: List<String> = emptyList(),
        equipment: List<String> = emptyList(),
        targetMuscles: List<String> = emptyList(),
        secondaryMuscles: List<String> = emptyList(),
        exerciseType: String? = null,
        instructions: String? = null,
        difficulty: String = "Facil",
        imageUrl: String? = null,
        imageFile: File? = null
    ): Exercise {
        return repository.updateLocalExercise(
            exerciseId = exerciseId,
            name = name,
            description = description,
            scheduledDays = scheduledDays,
            bodyparts = bodyparts,
            equipment = equipment,
            targetMuscles = targetMuscles,
            secondaryMuscles = secondaryMuscles,
            exerciseType = exerciseType,
            instructions = instructions,
            difficulty = difficulty,
            imageUrl = imageUrl,
            imageFile = imageFile
        )
    }
}

