package com.alilopez.kt_demohilt.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alilopez.kt_demohilt.core.database.converters.StringListConverters
import com.alilopez.kt_demohilt.core.database.dao.ExerciseDao
import com.alilopez.kt_demohilt.core.database.dao.PostDao
import com.alilopez.kt_demohilt.core.database.dao.RecipeDao
import com.alilopez.kt_demohilt.core.database.dao.RecipePlanDao
import com.alilopez.kt_demohilt.core.database.dao.WorkoutPlanDao
import com.alilopez.kt_demohilt.core.database.entities.ExerciseEntity
import com.alilopez.kt_demohilt.core.database.entities.PostEntity
import com.alilopez.kt_demohilt.core.database.entities.RecipeEntity
import com.alilopez.kt_demohilt.core.database.entities.RecipePlanEntity
import com.alilopez.kt_demohilt.core.database.entities.RecipePlanRecipeCrossRef
import com.alilopez.kt_demohilt.core.database.entities.WorkoutPlanEntity
import com.alilopez.kt_demohilt.core.database.entities.WorkoutPlanExerciseCrossRef

@Database(
    entities = [
        PostEntity::class,
        ExerciseEntity::class,
        RecipeEntity::class,
        RecipePlanEntity::class,
        WorkoutPlanEntity::class,
        RecipePlanRecipeCrossRef::class,
        WorkoutPlanExerciseCrossRef::class
    ],
    version = 4, // Incrementamos para las tablas de relación
    exportSchema = false
)
@TypeConverters(StringListConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun recipeDao(): RecipeDao
    abstract fun recipePlanDao(): RecipePlanDao
    abstract fun workoutPlanDao(): WorkoutPlanDao
}
