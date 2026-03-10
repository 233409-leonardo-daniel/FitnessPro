package com.alilopez.kt_demohilt.core.di

import com.alilopez.kt_demohilt.features.exercise.data.repositories.ExercisesRepositoryImpl
import com.alilopez.kt_demohilt.features.exercise.domain.repositories.ExerciseRepository
import com.alilopez.kt_demohilt.features.recipies.data.repositories.RecipeRepositoryImp
import com.alilopez.kt_demohilt.features.recipies.domain.repositories.RecipeRepository
import com.alilopez.kt_demohilt.features.user.data.repositories.UserRepositoryImp
import com.alilopez.kt_demohilt.features.user.domain.repositories.UserRepository
import com.alilopez.kt_demohilt.features.workoutplans.data.repositories.WorkoutPlanRepositoryImpl
import com.alilopez.kt_demohilt.features.workoutplans.domain.repositories.WorkoutPlanRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImp: UserRepositoryImp
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindRecipeRepository(
        recipeRepositoryImp: RecipeRepositoryImp
    ): RecipeRepository

    @Binds
    @Singleton
    abstract fun bindExerciseRepository(
        exerciseRepositoryImpl: ExercisesRepositoryImpl
    ): ExerciseRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutPlanRepository(
        workoutPlanRepositoryImpl: WorkoutPlanRepositoryImpl
    ): WorkoutPlanRepository
}
