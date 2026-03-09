package com.alilopez.kt_demohilt.features.exercise.data.repositories

import com.alilopez.kt_demohilt.core.network.FitnessProApi
import com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.domain.repositories.ExerciseRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class ExercisesRepositoryImpl @Inject constructor(
    private val api: FitnessProApi
) : ExerciseRepository {

    override suspend fun getExercises(): List<Exercise> {
        val response = api.getExercisesRemote(
            limit = 2
        )
        return response.data.map { it.toDomain() }
    }

    override suspend fun getExercisesByBodyPart(bodyPart: String): List<Exercise> {
        val response = api.getExercisesByBodyPartRemote(
            limit = 5,
            bodyPart = bodyPart
        )
        return response.data.map { it.toDomain() }
    }

    override suspend fun getLocalExercises(): List<Exercise> {
        return api.getExercisesLocal().map { it.toDomain() }
    }

    override suspend fun createLocalExercise(
        name: String,
        description: String,
        userId: Int,
        scheduledDays: List<String>,
        bodyparts: List<String>,
        equipment: List<String>,
        targetMuscles: List<String>,
        secondaryMuscles: List<String>,
        exerciseType: String?,
        instructions: String?,
        difficulty: String,
        imageFile: File?
    ): Exercise {
        val textType = "text/plain".toMediaTypeOrNull()

        val namePart = name.toRequestBody(textType)
        val descriptionPart = description.toRequestBody(textType)
        val userIdPart = userId.toString().toRequestBody(textType)

        val scheduledDaysPart = if (scheduledDays.isNotEmpty()) {
            scheduledDays.joinToString(",").toRequestBody(textType)
        } else null

        val bodypartsPart = if (bodyparts.isNotEmpty()) {
            bodyparts.joinToString(",").toRequestBody(textType)
        } else null

        val equipmentPart = if (equipment.isNotEmpty()) {
            equipment.joinToString(",").toRequestBody(textType)
        } else null

        val targetMusclesPart = if (targetMuscles.isNotEmpty()) {
            targetMuscles.joinToString(",").toRequestBody(textType)
        } else null

        val secondaryMusclesPart = if (secondaryMuscles.isNotEmpty()) {
            secondaryMuscles.joinToString(",").toRequestBody(textType)
        } else null

        val exerciseTypePart = exerciseType?.toRequestBody(textType)
        val instructionsPart = instructions?.toRequestBody(textType)
        val difficultyPart = difficulty.toRequestBody(textType)

        val imagePart = imageFile?.let { file ->
            val mediaType = "image/*".toMediaTypeOrNull()
            val requestFile = file.asRequestBody(mediaType)
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        }

        return api.createLocalExercise(
            name = namePart,
            description = descriptionPart,
            userId = userIdPart,
            scheduledDays = scheduledDaysPart,
            bodyparts = bodypartsPart,
            equipment = equipmentPart,
            targetMuscles = targetMusclesPart,
            secondaryMuscles = secondaryMusclesPart,
            exerciseType = exerciseTypePart,
            instructions = instructionsPart,
            difficulty = difficultyPart,
            image = imagePart
        ).toDomain()
    }
}

