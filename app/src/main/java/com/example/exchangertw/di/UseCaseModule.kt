package com.example.exchangertw.di

import com.example.exchangertw.domain.repository.RatesRepository
import com.example.exchangertw.domain.use_case.ExchangeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideExchangeUseCase(
        repository: RatesRepository
    ): ExchangeUseCase {
        return ExchangeUseCase(repository)
    }
}