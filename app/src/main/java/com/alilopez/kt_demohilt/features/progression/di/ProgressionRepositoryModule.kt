package com.alilopez.kt_demohilt.features.progression.di

import com.alilopez.kt_demohilt.features.progression.data.repositories.ProgressionRepositoryImpl
import com.alilopez.kt_demohilt.features.progression.domain.repositories.ProgressionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProgressionRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProgressionRepository(
        progressionRepositoryImpl: ProgressionRepositoryImpl
    ): ProgressionRepository
}
