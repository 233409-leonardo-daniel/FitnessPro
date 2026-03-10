package com.alilopez.kt_demohilt.core.di

import com.alilopez.kt_demohilt.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    @FitnessProRetrofit
    fun provideFitnessProApi(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.backend_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
