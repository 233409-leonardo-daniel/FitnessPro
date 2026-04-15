package com.alilopez.kt_demohilt.core.database.dao

import androidx.room.*
import com.alilopez.kt_demohilt.core.database.entities.RecipeEntity
import com.alilopez.kt_demohilt.core.database.entities.RecipePlanEntity
import com.alilopez.kt_demohilt.core.database.entities.RecipePlanRecipeCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipePlanDao {
    @Query("SELECT * FROM recipe_plans WHERE isDownloaded = 1")
    fun getDownloadedRecipePlans(): Flow<List<RecipePlanEntity>>

    @Query("SELECT id FROM recipe_plans")
    suspend fun getDownloadedPlanIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipePlan(plan: RecipePlanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipePlanRecipeCrossRef(crossRef: RecipePlanRecipeCrossRef)

    @Transaction
    @Query("""
        SELECT * FROM recipes 
        INNER JOIN RecipePlanRecipeCrossRef ON recipes.id = RecipePlanRecipeCrossRef.recipeId 
        WHERE RecipePlanRecipeCrossRef.planId = :planId
    """)
    fun getRecipesForPlan(planId: Int): Flow<List<RecipeEntity>>

    @Query("DELETE FROM recipe_plans WHERE id = :planId")
    suspend fun deleteRecipePlan(planId: Int)
    
    @Query("DELETE FROM RecipePlanRecipeCrossRef WHERE planId = :planId")
    suspend fun deleteRecipePlanCrossRefs(planId: Int)
}
