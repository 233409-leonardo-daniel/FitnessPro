package com.alilopez.kt_demohilt.core.di

import com.alilopez.kt_demohilt.BuildConfig
import com.alilopez.kt_demohilt.core.network.FitnessProApi
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
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFitnessProApi(retrofit: Retrofit): FitnessProApi {
        return retrofit.create(FitnessProApi::class.java)
    }
}
