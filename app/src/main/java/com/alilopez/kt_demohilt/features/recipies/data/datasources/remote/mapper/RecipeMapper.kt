package com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.mapper

import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.model.RecipeDto
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
        imageUrl = this.imageUrl
    )
}
