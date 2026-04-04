package com.alilopez.kt_demohilt.features.recipies.data.repositories

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
import java.io.File
import javax.inject.Inject

class RecipeRepositoryImp @Inject constructor(
    private val fitnessProApi: FitnessProApi,
    private val dao: RecipeDao
) : RecipeRepository {
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
        return dao.getDAORecipes().map { entities -> entities.map { it.toDomain() } }
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
        val imagePart = imageFile?.let { file ->
            val mediaType = "image/*".toMediaTypeOrNull()
            val requestFile = file.asRequestBody(mediaType)
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        }

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
}
