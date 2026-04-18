package com.alilopez.kt_demohilt.core.di

import android.content.Context
import androidx.room.Room
import com.alilopez.kt_demohilt.core.database.AppDatabase
import com.alilopez.kt_demohilt.core.database.dao.ExerciseDao
import com.alilopez.kt_demohilt.core.database.dao.PostDao
import com.alilopez.kt_demohilt.core.database.dao.RecipeDao
import com.alilopez.kt_demohilt.core.database.dao.RecipePlanDao
import com.alilopez.kt_demohilt.core.database.dao.WorkoutPlanDao

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fitnesspro_db"
        )
            .fallbackToDestructiveMigration() // Facilitamos el desarrollo con cambios de esquema
            .build()
    }

    @Provides
    fun providePostDao(db: AppDatabase) : PostDao = db.postDao()

    @Provides
    fun provideExerciseDao(db: AppDatabase) : ExerciseDao = db.exerciseDao()

    @Provides
    fun provideRecipeDao(db: AppDatabase) : RecipeDao = db.recipeDao()

    @Provides
    fun provideRecipePlanDao(db: AppDatabase) : RecipePlanDao = db.recipePlanDao()

    @Provides
    fun provideWorkoutPlanDao(db: AppDatabase) : WorkoutPlanDao = db.workoutPlanDao()
}
