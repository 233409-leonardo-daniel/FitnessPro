package com.alilopez.kt_demohilt.features.recipies.domain.usecases

import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe
import com.alilopez.kt_demohilt.features.recipies.domain.repositories.RecipeRepository
import javax.inject.Inject

class UpdateRecipeUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(
        recipeId: Int,
        name: String,
        description: String,
        ingredients: String,
        instructions: String,
        userId: Int? = null,
        scheduledDays: List<String> = emptyList(),
        mealType: String? = null,
        imageUrl: String? = null
    ): Recipe {
        return recipeRepository.updateRecipe(
            recipeId = recipeId,
            name = name,
            description = description,
            ingredients = ingredients,
            instructions = instructions,
            userId = userId,
            scheduledDays = scheduledDays,
            mealType = mealType,
            imageUrl = imageUrl
        )
    }
}
