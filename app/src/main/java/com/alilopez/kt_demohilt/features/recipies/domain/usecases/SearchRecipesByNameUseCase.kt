package com.alilopez.kt_demohilt.features.recipies.domain.usecases

import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe
import com.alilopez.kt_demohilt.features.recipies.domain.repositories.RecipeRepository
import javax.inject.Inject

class SearchRecipesByNameUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(name: String): List<Recipe> {
        return recipeRepository.searchRecipesByName(name)
    }
}

