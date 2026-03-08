package com.alilopez.kt_demohilt.core.network

import com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.model.ExercisesResponse
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.model.RecipeCreateDto
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.model.RecipeDto
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.UserCreateDto
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.UserDto
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.UserLoginResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FitnessProApi {
    @GET("recipes")
    suspend fun getRecipies(): List<RecipeDto>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): UserLoginResponseDto

    @GET("users")
    suspend fun getUser(
        @Query("id") id : Int
    ): UserDto

    @POST("users")
    suspend fun register(
        @Body user: UserCreateDto
    ): UserDto

    @POST("recipes")
    suspend fun createRecipe(
        @Body recipe: RecipeCreateDto
    ): RecipeDto

    @PUT("recipes/{recipe_id}")
    suspend fun updateRecipe(
        @Path("recipe_id") recipeId: Int,
        @Body recipe: RecipeCreateDto
    ): RecipeDto

    @DELETE("recipes/{recipe_id}")
    suspend fun deleteRecipe(
        @Path("recipe_id") recipeId: Int
    )

    @GET("exercises")
    suspend fun getExercises(
        @Query("limit") limit: Int
    ): ExercisesResponse

    @GET("exercises/filter")
    suspend fun getExercisesByBodyPart(
        @Query("limit") limit: Int,
        @Query("bodyParts") bodyPart: String
    ): ExercisesResponse
}
