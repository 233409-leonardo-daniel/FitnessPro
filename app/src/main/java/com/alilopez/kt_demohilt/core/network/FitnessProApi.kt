package com.alilopez.kt_demohilt.core.network

import com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.model.ExercisesResponse
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.model.RecipeCreateDto
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.model.RecipeDto
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.UserCreateDto
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.UserDto
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.UserLoginResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @Multipart
    @POST("recipes")
    suspend fun createRecipe(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("ingredients") ingredients: RequestBody,
        @Part("instructions") instructions: RequestBody,
        @Part("user_id") userId: RequestBody?,
        @Part("scheduled_days") scheduledDays: RequestBody?,
        @Part("meal_type") mealType: RequestBody?,
        @Part image: MultipartBody.Part?
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

    @GET("exercises/remote")
    suspend fun getExercisesRemote(
        @Query("limit") limit: Int
    ): ExercisesResponse

    @GET("exercises/bodyPart")
    suspend fun getExercisesByBodyPartRemote(
        @Query("limit") limit: Int,
        @Query("bodyParts") bodyPart: String
    ): ExercisesResponse

    @GET("exercises/local")
    suspend fun getExercisesLocal(): ExercisesResponse

//    @POST("exercises/local")
//    suspend fun addExerciseLocal(
//        @Body exercise: ExerciseRequest
//    )

    @GET("exercises/local/user/{user_id}")
    suspend fun getExercisesLocalByUserId(
        @Path("user_id") userId: Int
    ): ExercisesResponse

//    @PUT("exercises/local/{exercise_id}")
//    suspend fun updateExerciseLocal(
//        @Path("exercise_id") exerciseId: Int,
//        @Body exercise: ExerciseRequest
//    )


}
