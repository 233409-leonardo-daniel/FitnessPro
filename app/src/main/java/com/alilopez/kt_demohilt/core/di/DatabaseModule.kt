package com.alilopez.kt_demohilt.core.di

import android.content.Context
import androidx.room.Room
import com.alilopez.kt_demohilt.core.database.AppDatabase
import com.alilopez.kt_demohilt.core.database.dao.ExerciseDao
import com.alilopez.kt_demohilt.core.database.dao.PostDao
import com.alilopez.kt_demohilt.core.database.dao.RecipeDao

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
        ).build()
    }

    // Proveemos los DAOs individualmente
    // Esto permite que una Feature pida solo el DAO que le interesa

    @Provides
    fun providePostDao(db: AppDatabase) : PostDao = db.postDao()

    @Provides
    fun provideExerciseDao(db: AppDatabase) : ExerciseDao = db.exerciseDao()

    @Provides
    fun provideRecipeDao(db: AppDatabase) : RecipeDao = db.recipeDao()
}