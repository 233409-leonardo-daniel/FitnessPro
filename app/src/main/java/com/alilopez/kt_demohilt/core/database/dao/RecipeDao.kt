package com.alilopez.kt_demohilt.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.alilopez.kt_demohilt.core.database.entities.RecipeEntity

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes")
    suspend fun getDAORecipes(): List<RecipeEntity>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

}