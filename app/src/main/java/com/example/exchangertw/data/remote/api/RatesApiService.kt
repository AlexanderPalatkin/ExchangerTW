package com.example.exchangertw.data.remote.api

import com.example.exchangertw.data.remote.model.ExchangeRatesResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface RatesApiService {
    @GET("latest.js")
    fun getLatestRates(): Single<ExchangeRatesResponse>
}