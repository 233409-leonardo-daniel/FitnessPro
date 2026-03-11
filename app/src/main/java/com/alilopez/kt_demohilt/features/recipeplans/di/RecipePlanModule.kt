package com.alilopez.kt_demohilt.features.recipeplans.di

import com.alilopez.kt_demohilt.features.recipeplans.data.repositories.RecipePlanRepositoryImpl
import com.alilopez.kt_demohilt.features.recipeplans.domain.repositories.RecipePlanRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RecipePlanModule {

    @Binds
    @Singleton
    abstract fun bindRecipePlanRepository(
        recipePlanRepositoryImpl: RecipePlanRepositoryImpl
    ): RecipePlanRepository
}
