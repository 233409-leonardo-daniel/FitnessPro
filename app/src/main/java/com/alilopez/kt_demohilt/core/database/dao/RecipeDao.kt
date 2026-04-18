package com.alilopez.kt_demohilt.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alilopez.kt_demohilt.core.database.entities.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes")
    fun getDAORecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isDownloaded = 0")
    fun getSyncedRemoteRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isDownloaded = 1")
    fun getDownloadedRecipes(): Flow<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)
    
    @Query("UPDATE recipes SET isDownloaded = :isDownloaded WHERE id = :recipeId")
    suspend fun updateDownloadStatus(recipeId: Int, isDownloaded: Boolean)
}
