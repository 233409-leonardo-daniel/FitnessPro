package com.alilopez.kt_demohilt.features.exercise.domain.usecases

import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.domain.repositories.ExerciseRepository
import java.io.File
import javax.inject.Inject

class CreateLocalExerciseUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {
    suspend operator fun invoke(
        name: String,
        description: String,
        userId: Int,
        scheduledDays: List<String> = emptyList(),
        bodyparts: List<String> = emptyList(),
        equipment: List<String> = emptyList(),
        targetMuscles: List<String> = emptyList(),
        secondaryMuscles: List<String> = emptyList(),
        exerciseType: String? = null,
        instructions: String? = null,
        difficulty: String = "Facil",
        imageFile: File? = null
    ): Exercise {
        return repository.createLocalExercise(
            name = name,
            description = description,
            userId = userId,
            scheduledDays = scheduledDays,
            bodyparts = bodyparts,
            equipment = equipment,
            targetMuscles = targetMuscles,
            secondaryMuscles = secondaryMuscles,
            exerciseType = exerciseType,
            instructions = instructions,
            difficulty = difficulty,
            imageFile = imageFile
        )
    }
}

