package com.example.exchangertw.di

import com.example.exchangertw.data.local.dao.CurrencyDao
import com.example.exchangertw.data.remote.api.RatesApiService
import com.example.exchangertw.data.repository.RatesRepositoryImpl
import com.example.exchangertw.domain.repository.RatesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRatesRepository(
        apiService: RatesApiService,
        currencyDao: CurrencyDao
    ): RatesRepository {
        return RatesRepositoryImpl(apiService, currencyDao)
    }
}