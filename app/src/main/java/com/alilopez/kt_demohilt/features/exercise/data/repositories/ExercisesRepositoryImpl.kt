package com.alilopez.kt_demohilt.features.exercise.data.repositories

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.alilopez.kt_demohilt.core.database.dao.ExerciseDao
import com.alilopez.kt_demohilt.core.network.FitnessProApi
import com.alilopez.kt_demohilt.core.session.SessionManager
import com.alilopez.kt_demohilt.features.exercise.data.datasources.local.mapper.toDomain
import com.alilopez.kt_demohilt.features.exercise.data.datasources.local.mapper.toEntity
import com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.exercise.domain.entities.Exercise
import com.alilopez.kt_demohilt.features.exercise.domain.entities.ExerciseFilter
import com.alilopez.kt_demohilt.features.exercise.domain.entities.PaginatedExercises
import com.alilopez.kt_demohilt.features.exercise.domain.repositories.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class ExercisesRepositoryImpl @Inject constructor(
    private val api: FitnessProApi,
    private val dao : ExerciseDao,
    private val sessionManager: SessionManager
) : ExerciseRepository {
    companion object {
        private const val MAX_IMAGE_UPLOAD_BYTES = 1_500_000L
        private const val MAX_IMAGE_DIMENSION = 1600
        private const val START_IMAGE_QUALITY = 88
        private const val MIN_IMAGE_QUALITY = 55
    }
    override suspend fun getRemoteExercises(
        limit: Int,
        offset: Int?
    ): PaginatedExercises {
        val response = api.getExercisesRemote(
            limit = limit,
            offset = offset
        )
        return PaginatedExercises(
            exercises = response.data.map { it.toDomain() },
            hasNextPage = response.meta.hasNextPage,
            nextCursor = response.meta.nextCursor,
            total = response.meta.total
        )
    }

    override suspend fun getRemoteExercisesByBodyPart(
        bodyPart: String,
        limit: Int,
        offset: Int?
    ): PaginatedExercises {
        val response = api.getExercisesByBodyPartRemote(
            bodyPart = bodyPart,
            limit = limit,
            offset = offset
        )
        return PaginatedExercises(
            exercises = response.data.map { it.toDomain() },
            hasNextPage = response.meta.hasNextPage,
            nextCursor = response.meta.nextCursor,
            total = response.meta.total
        )
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

    override suspend fun updateOfflineAvailable(exerciseId: Int, isAvailable: Boolean) {
        dao.updateOfflineAvailable(exerciseId, isAvailable)
    }

    override fun getOfflineExercises(): Flow<List<Exercise>> {
        return dao.getOfflineExercises().map { entities -> entities.map { it.toDomain() } }
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
        imageUrl: String?,
        imageFile: File?
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
        val imagePart = imageFile?.let { createCompressedImagePart(it) }

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
            image = imagePart
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

        val imagePart = imageFile?.let { createCompressedImagePart(it) }

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

    private fun createCompressedImagePart(sourceFile: File): MultipartBody.Part {
        val fileForUpload = compressImageIfNeeded(sourceFile)
        val mediaType = "image/jpeg".toMediaTypeOrNull()
        val requestFile = fileForUpload.asRequestBody(mediaType)
        return MultipartBody.Part.createFormData("image", fileForUpload.name, requestFile)
    }

    private fun compressImageIfNeeded(sourceFile: File): File {
        if (!sourceFile.exists()) {
            throw IllegalArgumentException("No se encontro la imagen seleccionada")
        }

        if (sourceFile.length() <= MAX_IMAGE_UPLOAD_BYTES) {
            return sourceFile
        }

        val bitmap = decodeSampledBitmap(sourceFile) ?: throw IllegalArgumentException(
            "No se pudo procesar la imagen seleccionada"
        )

        var quality = START_IMAGE_QUALITY
        val compressedBytes = ByteArrayOutputStream()

        do {
            compressedBytes.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, compressedBytes)
            quality -= 8
        } while (compressedBytes.size() > MAX_IMAGE_UPLOAD_BYTES && quality >= MIN_IMAGE_QUALITY)

        bitmap.recycle()

        if (compressedBytes.size() > MAX_IMAGE_UPLOAD_BYTES) {
            throw IllegalArgumentException(
                "La imagen es demasiado pesada. Selecciona una mas ligera"
            )
        }

        val compressedFile = File.createTempFile(
            "exercise_upload_",
            ".jpg",
            sourceFile.parentFile
        )

        try {
            FileOutputStream(compressedFile).use { output ->
                output.write(compressedBytes.toByteArray())
                output.flush()
            }
        } catch (ioException: IOException) {
            compressedFile.delete()
            throw ioException
        }

        return compressedFile
    }

    private fun decodeSampledBitmap(sourceFile: File): Bitmap? {
        val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(sourceFile.absolutePath, boundsOptions)

        boundsOptions.inSampleSize = calculateInSampleSize(
            boundsOptions.outWidth,
            boundsOptions.outHeight,
            MAX_IMAGE_DIMENSION,
            MAX_IMAGE_DIMENSION
        )
        boundsOptions.inJustDecodeBounds = false

        return BitmapFactory.decodeFile(sourceFile.absolutePath, boundsOptions)
    }

    private fun calculateInSampleSize(
        width: Int,
        height: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        var sampleSize = 1
        var scaledWidth = width
        var scaledHeight = height

        while (scaledHeight > reqHeight || scaledWidth > reqWidth) {
            sampleSize *= 2
            scaledHeight /= 2
            scaledWidth /= 2
        }

        return sampleSize.coerceAtLeast(1)
    }
}
