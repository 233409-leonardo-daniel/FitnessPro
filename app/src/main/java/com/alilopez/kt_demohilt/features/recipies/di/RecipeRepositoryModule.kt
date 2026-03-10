package com.alilopez.kt_demohilt.features.recipies.di

import com.alilopez.kt_demohilt.features.recipies.data.repositories.RecipeRepositoryImp
import com.alilopez.kt_demohilt.features.recipies.domain.repositories.RecipeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RecipeRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRecipeRepository(
        recipeRepositoryImp: RecipeRepositoryImp
    ): RecipeRepository
}

