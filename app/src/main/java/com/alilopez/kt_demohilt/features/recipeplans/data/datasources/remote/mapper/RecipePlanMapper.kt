package com.alilopez.kt_demohilt.features.recipeplans.data.datasources.remote.mapper

import com.alilopez.kt_demohilt.core.database.entities.RecipePlanEntity
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.mapper.toDomain
import com.alilopez.kt_demohilt.features.recipeplans.data.datasources.remote.model.RecipePlanDto
import com.alilopez.kt_demohilt.features.recipeplans.domain.entities.RecipePlan

fun RecipePlanDto.toDomain(): RecipePlan {
    return RecipePlan(
        id = this.id,
        name = this.name,
        description = this.description,
        userId = this.userId,
        isPrivate = this.private,
        recipes = this.recipes.map { it.toDomain() }
    )
}

fun RecipePlanEntity.toDomain(): RecipePlan {
    return RecipePlan(
        id = this.id,
        name = this.name,
        description = this.description,
        userId = this.userId,
        isPrivate = this.isPrivate,
        recipes = emptyList() // Las recetas se cargan por separado si es necesario
    )
}
