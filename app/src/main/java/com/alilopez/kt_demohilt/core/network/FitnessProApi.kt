package com.alilopez.kt_demohilt.core.network

import com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.model.ExercisesResponse
import com.alilopez.kt_demohilt.features.exercise.data.datasources.remote.model.LocalExerciseDto
import com.alilopez.kt_demohilt.features.workoutplans.data.datasources.remote.model.AddExerciseToPlanDto
import com.alilopez.kt_demohilt.features.workoutplans.data.datasources.remote.model.WorkoutPlanCreateDto
import com.alilopez.kt_demohilt.features.workoutplans.data.datasources.remote.model.WorkoutPlanDto
import com.alilopez.kt_demohilt.features.recipeplans.data.datasources.remote.model.AddRecipeToPlanDto
import com.alilopez.kt_demohilt.features.recipeplans.data.datasources.remote.model.RecipePlanCreateDto
import com.alilopez.kt_demohilt.features.recipeplans.data.datasources.remote.model.RecipePlanDto
import com.alilopez.kt_demohilt.features.recipies.data.datasources.remote.model.RecipeDto
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.UserCreateDto
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.UserDto
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.UserLoginResponseDto
import com.alilopez.kt_demohilt.features.user.data.datasources.remote.model.UserUpdateDto
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
    suspend fun getRecipes(): List<RecipeDto>

    @GET("recipes/{recipe_id}")
    suspend fun getRecipeDetail(
        @Path("recipe_id") recipeId: Int
    ): RecipeDto

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): UserLoginResponseDto

    @FormUrlEncoded
    @POST("login/google")
    suspend fun loginWithGoogle(
        @Field("id_token") idToken: String
    ): UserLoginResponseDto

    @GET("users/{id}")
    suspend fun getUser(
        @Path("id") id : Int
    ): UserDto

    @POST("users")
    suspend fun register(
        @Body user: UserCreateDto
    ): UserDto

    @PUT("users/{user_id}")
    suspend fun updateUser(
        @Path("user_id") userId: Int,
        @Body user: UserUpdateDto
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
        @Part image: MultipartBody.Part?,
        @Part audio: MultipartBody.Part?
    ): RecipeDto

    @Multipart
    @PUT("recipes/{recipe_id}")
    suspend fun updateRecipe(
        @Path("recipe_id") recipeId: Int,
        @Part("name") name: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("ingredients") ingredients: RequestBody?,
        @Part("instructions") instructions: RequestBody?,
        @Part("scheduled_days") scheduledDays: RequestBody?,
        @Part("meal_type") mealType: RequestBody?,
        @Part("image_url") imageUrl: RequestBody?,
        @Part("audio_url") audioUrl: RequestBody?,
        @Part image: MultipartBody.Part?,
        @Part audio: MultipartBody.Part?
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
    suspend fun getExercisesLocal(): List<LocalExerciseDto>

    @Multipart
    @POST("exercises/local")
    suspend fun createLocalExercise(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part("scheduled_days") scheduledDays: RequestBody?,
        @Part("bodyparts") bodyparts: RequestBody?,
        @Part("equipment") equipment: RequestBody?,
        @Part("target_muscles") targetMuscles: RequestBody?,
        @Part("secondary_muscles") secondaryMuscles: RequestBody?,
        @Part("exercise_type") exerciseType: RequestBody?,
        @Part("instructions") instructions: RequestBody?,
        @Part("difficulty") difficulty: RequestBody?,
        @Part image: MultipartBody.Part?
    ): LocalExerciseDto

    @GET("exercises/local/user/{user_id}")
    suspend fun getExercisesLocalByUserId(
        @Path("user_id") userId: Int
    ): List<LocalExerciseDto>

    // --- WORKOUT PLANS ---

    @GET("workout_plans/user/{user_id}")
    suspend fun getUserWorkoutPlans(
        @Path("user_id") userId: Int
    ): List<WorkoutPlanDto>

    @POST("workout_plans/")
    suspend fun createWorkoutPlan(
        @Body workoutPlan: WorkoutPlanCreateDto
    ): WorkoutPlanDto

    @POST("workout_plans/{plan_id}/exercises")
    suspend fun addExerciseToPlan(
        @Path("plan_id") planId: Int,
        @Body exercise: AddExerciseToPlanDto
    ): WorkoutPlanDto

    @GET("workout_plans/{plan_id}/exercises")
    suspend fun getPlanExercises(
        @Path("plan_id") planId: Int
    ): List<LocalExerciseDto>

    @DELETE("workout_plans/{plan_id}")
    suspend fun deleteWorkoutPlan(
        @Path("plan_id") planId: Int
    )

    @DELETE("workout_plans/{plan_id}/exercises/{exercise_id}")
    suspend fun removeExerciseFromPlan(
        @Path("plan_id") planId: Int,
        @Path("exercise_id") exerciseId: Int
    ): WorkoutPlanDto

    @GET("exercises/local/bodypart/{bodypart}")
    suspend fun getExercisesLocalByBodyPart(
        @Path("bodypart") bodyPart: String
    ): List<LocalExerciseDto>

    // --- RECIPE PLANS ---

    @GET("recipe_plans/user/{user_id}")
    suspend fun getUserRecipePlans(
        @Path("user_id") userId: Int
    ): List<RecipePlanDto>

    @POST("recipe_plans/")
    suspend fun createRecipePlan(
        @Body recipePlan: RecipePlanCreateDto
    ): RecipePlanDto

    @POST("recipe_plans/{plan_id}/recipes")
    suspend fun addRecipeToPlan(
        @Path("plan_id") planId: Int,
        @Body recipe: AddRecipeToPlanDto
    ): RecipePlanDto

    @GET("recipe_plans/{plan_id}/recipes")
    suspend fun getPlanRecipes(
        @Path("plan_id") planId: Int
    ): List<RecipeDto>

    @DELETE("recipe_plans/{plan_id}")
    suspend fun deleteRecipePlan(
        @Path("plan_id") planId: Int
    )

    @DELETE("recipe_plans/{plan_id}/recipes/{recipe_id}")
    suspend fun removeRecipeFromPlan(
        @Path("plan_id") planId: Int,
        @Path("recipe_id") recipeId: Int
    ): RecipePlanDto

    @GET("recipes/search/{name}")
    suspend fun searchRecipes(
        @Path("name") name: String
    ): List<RecipeDto>

    @GET("exercises/local/search/{name}")
    suspend fun searchLocalExercises(
        @Path("name") name: String
    ): List<LocalExerciseDto>
}
