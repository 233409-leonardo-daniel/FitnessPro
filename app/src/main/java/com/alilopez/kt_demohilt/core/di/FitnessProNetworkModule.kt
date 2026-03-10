package com.alilopez.kt_demohilt.core.di

import com.alilopez.kt_demohilt.core.network.FitnessProApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FitnessProNetworkModule {
    @Provides
    @Singleton
    fun provideFitnessProApi(@FitnessProRetrofit retrofit: Retrofit): FitnessProApi {
        return retrofit.create(FitnessProApi::class.java)
    }
}