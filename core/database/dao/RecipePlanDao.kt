package com.alilopez.kt_demohilt.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alilopez.kt_demohilt.core.database.entities.RecipePlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipePlanDao {
    @Query("SELECT * FROM recipe_plans WHERE isDownloaded = 1")
    fun getDownloadedRecipePlans(): Flow<List<RecipePlanEntity>>

    @Query("SELECT id FROM recipe_plans")
    suspend fun getDownloadedPlanIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipePlan(plan: RecipePlanEntity)

    @Query("DELETE FROM recipe_plans WHERE id = :planId")
    suspend fun deleteRecipePlan(planId: Int)
}
