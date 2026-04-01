package com.alilopez.kt_demohilt.features.user.di

import com.alilopez.kt_demohilt.features.user.data.repositories.PaymentRepositoryImp
import com.alilopez.kt_demohilt.features.user.data.repositories.UserRepositoryImp
import com.alilopez.kt_demohilt.features.user.domain.repositories.PaymentRepository
import com.alilopez.kt_demohilt.features.user.domain.repositories.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImp: UserRepositoryImp
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        paymentRepositoryImp: PaymentRepositoryImp
    ): PaymentRepository
}
