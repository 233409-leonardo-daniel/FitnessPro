package com.alilopez.kt_demohilt.features.recipies.data.repositories

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.alilopez.kt_demohilt.core.database.dao.RecipeDao
import com.alilopez.kt_demohilt.core.network.FitnessProApi
import com.alilopez.kt_demohilt.features.recipies.data.datasources.local.mapper.toDomain
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.mapper.toEntity
import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe
import com.alilopez.kt_demohilt.features.recipies.domain.repositories.RecipeRepository
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

class RecipeRepositoryImp @Inject constructor(
    private val fitnessProApi: FitnessProApi,
    private val dao: RecipeDao
) : RecipeRepository {
    companion object {
        private const val MAX_IMAGE_UPLOAD_BYTES = 1_500_000L
        private const val MAX_IMAGE_DIMENSION = 1600
        private const val START_IMAGE_QUALITY = 88
        private const val MIN_IMAGE_QUALITY = 55
    }

    override suspend fun getRecipes(): List<Recipe> {
        return fitnessProApi.getRecipes().map { it.toDomain() }
    }

    override suspend fun searchRecipesByName(name: String): List<Recipe> {
        return fitnessProApi.searchRecipes(name).map { it.toDomain() }
    }

    override suspend fun getRecipeDetail(recipeId: Int): Recipe {
        return fitnessProApi.getRecipeDetail(recipeId).toDomain()
    }

    override suspend fun getUserRecipes(userId: Int): List<Recipe> {
        return fitnessProApi.getUserRecipes(userId).map { it.toDomain() }
    }

    override suspend fun getCommunityRecipes(userId: Int): List<Recipe> {
        return fitnessProApi.getCommunityRecipes(userId).map { it.toDomain() }
    }

    override fun getRemoteRecipes(): Flow<List<Recipe>> {
        return dao.getSyncedRemoteRecipes().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun syncRecipes() {
        try {
            // El 5 esta hardcodeado debido al delay que tiene la API, si se pide más de 5 recetas, el tiempo de respuesta es muy alto y puede causar timeouts
            val remoteRecipes = fitnessProApi.getRandomRemoteRecipes(5).recipes.map { it.toDomain() }
            dao.insertRecipes(remoteRecipes.map { it.toEntity() })
        } catch (e: Exception) {
            Log.e("Archivo:RecipeRepositoryImp", "Error syncing recipes: ${e.message}")
        }
    }

    override suspend fun createRecipe(
        name: String,
        description: String,
        ingredients: String,
        instructions: String,
        userId: Int?,
        scheduledDays: List<String>,
        mealType: String?,
        imageFile: File?,
        audioFile: File?
    ): Recipe {
        val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val ingredientsPart = ingredients.toRequestBody("text/plain".toMediaTypeOrNull())
        val instructionsPart = instructions.toRequestBody("text/plain".toMediaTypeOrNull())
        val userIdPart = userId?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val scheduledDaysPart = if (scheduledDays.isNotEmpty()) {
            scheduledDays.joinToString(",").toRequestBody("text/plain".toMediaTypeOrNull())
        } else null
        val mealTypePart = mealType?.toRequestBody("text/plain".toMediaTypeOrNull())

        val imagePart = imageFile?.let { createCompressedImagePart(it) }

        val audioPart = audioFile?.let { file ->
            val mediaType = "audio/mp4".toMediaTypeOrNull()
            val requestFile = file.asRequestBody(mediaType)
            MultipartBody.Part.createFormData("audio", file.name, requestFile)
        }

        return fitnessProApi.createRecipe(
            name = namePart,
            description = descriptionPart,
            ingredients = ingredientsPart,
            instructions = instructionsPart,
            userId = userIdPart,
            scheduledDays = scheduledDaysPart,
            mealType = mealTypePart,
            image = imagePart,
            audio = audioPart
        ).toDomain()
    }

    override suspend fun updateRecipe(
        recipeId: Int,
        name: String,
        description: String,
        ingredients: String,
        instructions: String,
        userId: Int?,
        scheduledDays: List<String>,
        mealType: String?,
        imageUrl: String?,
        imageFile: File?
    ): Recipe {
        val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val ingredientsPart = ingredients.toRequestBody("text/plain".toMediaTypeOrNull())
        val instructionsPart = instructions.toRequestBody("text/plain".toMediaTypeOrNull())
        val scheduledDaysPart = if (scheduledDays.isNotEmpty()) {
            scheduledDays.joinToString(",").toRequestBody("text/plain".toMediaTypeOrNull())
        } else null
        val mealTypePart = mealType?.toRequestBody("text/plain".toMediaTypeOrNull())
        val imageUrlPart = imageUrl?.toRequestBody("text/plain".toMediaTypeOrNull())
        val imagePart = imageFile?.let { createCompressedImagePart(it) }

        return fitnessProApi.updateRecipe(
            recipeId = recipeId,
            name = namePart,
            description = descriptionPart,
            ingredients = ingredientsPart,
            instructions = instructionsPart,
            scheduledDays = scheduledDaysPart,
            mealType = mealTypePart,
            imageUrl = imageUrlPart,
            audioUrl = null,
            image = imagePart,
            audio = null
        ).toDomain()
    }

    override suspend fun deleteRecipe(recipeId: Int) {
        fitnessProApi.deleteRecipe(recipeId)
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
            "recipe_upload_",
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
