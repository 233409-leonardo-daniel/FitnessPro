package com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.mapper

import com.alilopez.kt_demohilt.core.database.entities.RecipeEntity
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.model.RecipeDto
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.model.RemoteRecipeDto
import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe

fun RecipeDto.toDomain(): Recipe {
    return Recipe(
        id = this.id,
        name = this.name.orEmpty(),
        description = this.description.orEmpty(),
        ingredients = this.ingredients.orEmpty(),
        instructions = this.instructions.orEmpty(),
        userId = this.userId,
        scheduledDays = this.scheduledDays ?: emptyList(),
        mealType = this.mealType,
        imageUrl = this.imageUrl,
        audioUrl = this.audioUrl
    )
}

fun RemoteRecipeDto.toDomain(): Recipe {
    return Recipe(
        id = id,
        name = name,
        description = "",
        ingredients = ingredients.joinToString(separator = ", "),
        instructions = instructions.orEmpty(),
        userId = null,
        scheduledDays = emptyList(),
        mealType = category,
        imageUrl = imageUrl,
        audioUrl = null
    )
}

fun Recipe.toEntity() = RecipeEntity(
    id = id,
    name = name,
    measures = description.split(",").map { it.trim() },
    ingredients = ingredients.split(",").map { it.trim() },
    instructions = instructions,
    imageUrl = imageUrl ?: "",
    category = mealType ?: "",
    area = "",
    tags = emptyList(),
    youtubeUrl = "",
    sourceUrl = ""
)