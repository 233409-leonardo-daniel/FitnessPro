package com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.mapper

import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.model.RecipeDto
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.model.RemoteRecipeDto
import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe

fun RecipeDto.toDomain(): Recipe {
    return Recipe(
        id = this.id,
        name = this.name,
        description = this.description,
        ingredients = this.ingredients,
        instructions = this.instructions,
        userId = this.userId,
        scheduledDays = this.scheduledDays,
        mealType = this.mealType,
        imageUrl = this.imageUrl,
        audioUrl = this.audioUrl
    )
}

fun RemoteRecipeDto.toDomain(): Recipe {
    val normalizedDescription = listOfNotNull(category, area)
        .joinToString(separator = " | ")

    return Recipe(
        id = id.toIntOrNull() ?: 0,
        name = name,
        description = normalizedDescription,
        ingredients = ingredients.joinToString(separator = ", "),
        instructions = instructions.orEmpty(),
        userId = null,
        scheduledDays = emptyList(),
        mealType = category,
        imageUrl = imageUrl,
        audioUrl = youtubeUrl
    )
}
