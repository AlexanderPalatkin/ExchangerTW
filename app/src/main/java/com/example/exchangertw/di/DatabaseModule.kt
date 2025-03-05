package com.example.exchangertw.di

import android.content.Context
import com.example.exchangertw.data.local.dao.CurrencyDao
import com.example.exchangertw.data.local.db.CurrencyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCurrencyDatabase(
        @ApplicationContext context: Context
    ): CurrencyDatabase {
        return CurrencyDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideCurrencyDao(database: CurrencyDatabase): CurrencyDao {
        return database.currencyDao()
    }
}