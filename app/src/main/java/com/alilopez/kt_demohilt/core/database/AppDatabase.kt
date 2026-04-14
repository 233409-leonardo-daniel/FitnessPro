package com.alilopez.kt_demohilt.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.alilopez.kt_demohilt.core.database.converters.StringListConverters
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
    version = 3, // Versión 3: Añadido campo offline_available a exercises
    exportSchema = false
)
@TypeConverters(StringListConverters::class)

abstract class AppDatabase : RoomDatabase() {
    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `recipes` (
                        `id` INTEGER NOT NULL,
                        `name` TEXT NOT NULL,
                        `instructions` TEXT NOT NULL,
                        `ingredients` TEXT NOT NULL,
                        `measures` TEXT NOT NULL,
                        `imageUrl` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `area` TEXT NOT NULL,
                        `tags` TEXT NOT NULL,
                        `youtubeUrl` TEXT NOT NULL,
                        `sourceUrl` TEXT NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE exercises ADD COLUMN offline_available INTEGER NOT NULL DEFAULT 0")
            }
        }
    }

    abstract fun postDao(): PostDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun recipeDao(): RecipeDao
}