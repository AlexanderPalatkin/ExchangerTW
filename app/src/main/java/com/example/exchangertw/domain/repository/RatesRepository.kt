package com.example.exchangertw.domain.repository

import com.example.exchangertw.domain.model.Currency
import com.example.exchangertw.domain.model.ErrorType
import com.example.exchangertw.domain.model.ResultState
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface RatesRepository {
    fun getLatestRates(): Single<ResultState<List<Currency>, ErrorType>>

    fun updateCurrencyAmount(code: String, newAmount: Double, isSell: Boolean): Completable
}