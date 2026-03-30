package com.alilopez.kt_demohilt.features.recipies.domain.usecases

import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe
import com.alilopez.kt_demohilt.features.recipies.domain.repositories.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRemoteRecipesUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
     operator fun invoke(): Flow<List<Recipe>> {
        return recipeRepository.getRemoteRecipes()
    }
}