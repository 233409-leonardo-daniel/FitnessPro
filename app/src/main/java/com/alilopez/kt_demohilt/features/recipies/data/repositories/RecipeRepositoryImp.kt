package com.alilopez.kt_demohilt.features.recipies.data.repositories

import com.alilopez.kt_demohilt.core.network.FitnessProApi
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.model.RecipeCreateDto
import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe
import com.alilopez.kt_demohilt.features.recipies.domain.repositories.RecipeRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class RecipeRepositoryImp @Inject constructor(
    private val fitnessProApi: FitnessProApi
) : RecipeRepository {
    override suspend fun getRecipies(): List<Recipe> {
        return fitnessProApi.getRecipes().map { it.toDomain() }
    }

    override suspend fun getRecipeDetail(recipeId: Int): Recipe {
        return fitnessProApi.getRecipeDetail(recipeId).toDomain()
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

        val imagePart = imageFile?.let { file ->
            val mediaType = "image/*".toMediaTypeOrNull()
            val requestFile = file.asRequestBody(mediaType)
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        }

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
        imageUrl: String?
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
            image = null,
            audio = null
        ).toDomain()
    }

    override suspend fun deleteRecipe(recipeId: Int) {
        fitnessProApi.deleteRecipe(recipeId)
    }
}
