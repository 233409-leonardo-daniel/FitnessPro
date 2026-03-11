package com.alilopez.kt_demohilt.features.workoutplans.di

import com.alilopez.kt_demohilt.features.workoutplans.data.repositories.WorkoutPlanRepositoryImpl
import com.alilopez.kt_demohilt.features.workoutplans.domain.repositories.WorkoutPlanRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkoutPlanModule {

    @Binds
    @Singleton
    abstract fun bindWorkoutPlanRepository(
        workoutPlanRepositoryImpl: WorkoutPlanRepositoryImpl
    ): WorkoutPlanRepository
}
