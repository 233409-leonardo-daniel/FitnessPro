package com.alilopez.kt_demohilt.core.di

import com.alilopez.kt_demohilt.core.hardware.data.AndroidCameraPhotoManager
import com.alilopez.kt_demohilt.core.hardware.data.AndroidFlashManager
import com.alilopez.kt_demohilt.core.hardware.domain.CameraPhotoManager
import com.alilopez.kt_demohilt.core.hardware.domain.FlashManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HardwareModule {

    @Binds
    @Singleton
    abstract fun bindFlashManager(
        impl: AndroidFlashManager
    ): FlashManager

    @Binds
    @Singleton
    abstract fun bindCameraPhotoManager(
        impl: AndroidCameraPhotoManager
    ): CameraPhotoManager
}