package com.alilopez.kt_demohilt.features.recipies.domain.usecases

import com.alilopez.kt_demohilt.features.recipies.domain.entities.Recipe
import com.alilopez.kt_demohilt.features.recipies.domain.repositories.RecipeRepository
import java.io.File
import javax.inject.Inject

class CreateRecipeUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(
        name: String,
        description: String,
        ingredients: String,
        instructions: String,
        userId: Int? = null,
        scheduledDays: List<String> = emptyList(),
        mealType: String? = null,
        imageFile: File? = null,
        audioFile: File? = null
    ): Recipe {
        return recipeRepository.createRecipe(
            name = name,
            description = description,
            ingredients = ingredients,
            instructions = instructions,
            userId = userId,
            scheduledDays = scheduledDays,
            mealType = mealType,
            imageFile = imageFile,
            audioFile = audioFile
        )
    }
}
