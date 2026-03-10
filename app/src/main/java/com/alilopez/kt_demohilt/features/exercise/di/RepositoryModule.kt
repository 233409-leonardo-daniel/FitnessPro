package com.alilopez.kt_demohilt.features.exercise.di

import com.alilopez.kt_demohilt.features.exercise.data.repositories.ExercisesRepositoryImpl
import com.alilopez.kt_demohilt.features.exercise.domain.repositories.ExerciseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ExerciseRepositoryModule {
    @Binds
    abstract fun bindExerciseRepository(
        exerciseRepositoryImpl: ExercisesRepositoryImpl
    ): ExerciseRepository
}