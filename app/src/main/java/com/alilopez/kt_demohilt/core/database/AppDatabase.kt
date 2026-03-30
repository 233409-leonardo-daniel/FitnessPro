package com.alilopez.kt_demohilt.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alilopez.kt_demohilt.core.database.dao.ExerciseDao
import com.alilopez.kt_demohilt.core.database.dao.PostDao
import com.alilopez.kt_demohilt.core.database.dao.RecipeDao
import com.alilopez.kt_demohilt.core.database.entities.ExerciseEntity
import com.alilopez.kt_demohilt.core.database.entities.PostEntity
import com.alilopez.kt_demohilt.core.database.entities.RecipeEntity

@Database(
    entities = [
        PostEntity::class,
        ExerciseEntity::class,
        RecipeEntity::class
               ],
    version = 2, // Versión inicial (clave para migraciones futuras)
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun recipeDao(): RecipeDao
}