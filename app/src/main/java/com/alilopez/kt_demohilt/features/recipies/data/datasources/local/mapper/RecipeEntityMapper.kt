package com.alilopez.kt_demohilt.features.recipies.data.datasources.local.mapper

import com.alilopez.kt_demohilt.core.database.entities.RecipeEntity
import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe

fun RecipeEntity.toDomain() = Recipe(
    id = id,
    name = name,
    description = measures.joinToString(", "), // Se puso en description para no perder la información, pero se puede ajustar según el diseño
    ingredients = ingredients.joinToString(", "), //Viene en lista, se convierte a String para el dominio
    instructions = instructions,
    userId = null,
    scheduledDays = null,
    mealType = category,
    imageUrl = imageUrl,
    audioUrl = null
)