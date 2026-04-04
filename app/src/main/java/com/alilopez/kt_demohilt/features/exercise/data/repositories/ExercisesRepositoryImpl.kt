package com.alilopez.kt_demohilt.features.exercise.data.repositories

import android.util.Log
import com.alilopez.kt_demohilt.core.database.dao.ExerciseDao
import com.alilopez.kt_demohilt.core.network.FitnessProApi
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.features.exercise.data.datasources.local.mapper.toDomain
import com.alilopez.kt_demohilt.features.exercise.data.datasources.local.mapper.toEntity
import com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.domain.entities.ExerciseFilter
import com.alilopez.kt_demohilt.features.exercise.domain.repositories.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class ExercisesRepositoryImpl @Inject constructor(
    private val api: FitnessProApi,
    private val dao : ExerciseDao,
    private val sessionManager: SessionManager
) : ExerciseRepository {

    override suspend fun getRemoteExercises(): List<Exercise> {
        val response = api.getExercisesRemote(
            limit = 25
        )
        return response.data.map { it.toDomain() }
    }

    override suspend fun getRemoteExercisesByBodyPart(bodyPart: String): List<Exercise> {
        val response = api.getExercisesByBodyPartRemote(
            limit = 25,
            bodyPart = bodyPart
        )
        return response.data.map { it.toDomain() }
    }

    override suspend fun getCommunityExercises(userId: Int): List<Exercise> {
        return api.getCommunityExercises(userId).map { it.toDomain() }
    }

    override suspend fun searchLocalExercisesByName(name: String): List<Exercise> {
        return api.searchLocalExercises(name).map { it.toDomain() }
    }

    override fun getLocalExercisesByFilter(filter: ExerciseFilter): Flow<List<Exercise>> {
        return dao.getExercisesByFilter(
            bodyPart = filter.bodyPart,
            difficulty = filter.difficulty,
            exerciseType = filter.exerciseType
        ).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun syncExercisesByFilter(filter: ExerciseFilter) {
        try {
            filter.bodyPart?.let { bodyPart ->
                val remoteExercises = api.getExercisesLocalByBodyPart(
                    bodyPart = bodyPart
                )
                val entities = remoteExercises.map { it.toDomain().toEntity() }
                dao.insertExercises(entities)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun getLocalExercises(): Flow<List<Exercise>> {
        return dao.getAllExercises().map { entities -> entities.map {
            it.toDomain()
        } }
    }

    override suspend fun getExercisesByUserId(userId: Int): List<Exercise> {
        return api.getExercisesLocalByUserId(userId).map { it.toDomain() }
    }

    override suspend fun getExerciseById(exerciseId: Int): Exercise {
        return api.getExerciseById(exerciseId).toDomain()
    }

    override suspend fun updateLocalExercise(
        exerciseId: Int,
        name: String,
        description: String,
        scheduledDays: List<String>,
        bodyparts: List<String>,
        equipment: List<String>,
        targetMuscles: List<String>,
        secondaryMuscles: List<String>,
        exerciseType: String?,
        instructions: String?,
        difficulty: String,
        imageUrl: String?
    ): Exercise {
        val textType = "text/plain".toMediaTypeOrNull()

        val namePart = name.toRequestBody(textType)
        val descriptionPart = description.toRequestBody(textType)
        val scheduledDaysPart = scheduledDays.takeIf { it.isNotEmpty() }?.joinToString(",")?.toRequestBody(textType)
        val bodypartsPart = bodyparts.takeIf { it.isNotEmpty() }?.joinToString(",")?.toRequestBody(textType)
        val equipmentPart = equipment.takeIf { it.isNotEmpty() }?.joinToString(",")?.toRequestBody(textType)
        val targetMusclesPart = targetMuscles.takeIf { it.isNotEmpty() }?.joinToString(",")?.toRequestBody(textType)
        val secondaryMusclesPart = secondaryMuscles.takeIf { it.isNotEmpty() }?.joinToString(",")?.toRequestBody(textType)
        val exerciseTypePart = exerciseType?.toRequestBody(textType)
        val instructionsPart = instructions?.toRequestBody(textType)
        val difficultyPart = difficulty.toRequestBody(textType)
        val imageUrlPart = imageUrl?.toRequestBody(textType)

        return api.updateLocalExercise(
            exerciseId = exerciseId,
            name = namePart,
            description = descriptionPart,
            scheduledDays = scheduledDaysPart,
            bodyparts = bodypartsPart,
            equipment = equipmentPart,
            targetMuscles = targetMusclesPart,
            secondaryMuscles = secondaryMusclesPart,
            exerciseType = exerciseTypePart,
            instructions = instructionsPart,
            difficulty = difficultyPart,
            imageUrl = imageUrlPart,
            image = null
        ).toDomain()
    }

    override suspend fun deleteExercise(exerciseId: Int) {
        api.deleteExercise(exerciseId)
    }

    override suspend fun syncExercises() {
        Log.d("ExercisesRepository", "Iniciando syncExercises...")
        try {
            val userId = sessionManager.currentUserId
            Log.d("ExercisesRepository", "UserId obtenido: $userId")
            
            if (userId != null) {
                Log.d("ExercisesRepository", "Llamando a api.getExercisesLocalByUserId($userId)")
                val remoteExercises = api.getExercisesLocalByUserId(userId)
                Log.d("ExercisesRepository", "Ejercicios recibidos: ${remoteExercises.size}")
                
                val entities = remoteExercises.map { it.toDomain().toEntity() }
                dao.insertExercises(entities)
                Log.d("ExercisesRepository", "Ejercicios insertados en DB local")
            } else {
                Log.e("ExercisesRepository", "No se pudo sincronizar: UserId es NULL")
            }
        } catch (e: Exception) {
            Log.e("ExercisesRepository", "Error en syncExercises: ${e.message}")
            e.printStackTrace()
        }
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
