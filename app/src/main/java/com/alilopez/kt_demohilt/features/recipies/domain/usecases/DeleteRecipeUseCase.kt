package com.alilopez.kt_demohilt.features.recipies.domain.usecases

import com.alilopez.kt_demohilt.features.recipies.domain.repositories.RecipeRepository
import javax.inject.Inject

class DeleteRecipeUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(recipeId: Int) {
        recipeRepository.deleteRecipe(recipeId)
    }
}
