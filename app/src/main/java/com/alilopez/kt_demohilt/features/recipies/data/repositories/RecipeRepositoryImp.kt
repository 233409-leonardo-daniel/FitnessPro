package com.alilopez.kt_demohilt.features.recipies.data.repositories

import com.alilopez.kt_demohilt.core.network.FitnessProApi
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.model.RecipeCreateDto
import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe
import com.alilopez.kt_demohilt.features.recipies.domain.repositories.RecipeRepository
import javax.inject.Inject

class RecipeRepositoryImp @Inject constructor(
    private val fitnessProApi: FitnessProApi
) : RecipeRepository {
    override suspend fun getRecipies(): List<Recipe> {
        return fitnessProApi.getRecipies().map { it.toDomain() }
    }

    override suspend fun createRecipe(
        name: String,
        description: String,
        ingredients: String,
        instructions: String,
        userId: Int?,
        scheduledDatetime: String?
    ): Recipe {
        val recipeCreateDto = RecipeCreateDto(
            name = name,
            description = description,
            ingredients = ingredients,
            instructions = instructions,
            userId = userId,
            scheduledDatetime = scheduledDatetime
        )
        return fitnessProApi.createRecipe(recipeCreateDto).toDomain()
    }

    override suspend fun updateRecipe(
        recipeId: Int,
        name: String,
        description: String,
        ingredients: String,
        instructions: String,
        userId: Int?,
        scheduledDatetime: String?
    ): Recipe {
        val recipeCreateDto = RecipeCreateDto(
            name = name,
            description = description,
            ingredients = ingredients,
            instructions = instructions,
            userId = userId,
            scheduledDatetime = scheduledDatetime
        )
        return fitnessProApi.updateRecipe(recipeId, recipeCreateDto).toDomain()
    }

    override suspend fun deleteRecipe(recipeId: Int) {
        fitnessProApi.deleteRecipe(recipeId)
    }
}
