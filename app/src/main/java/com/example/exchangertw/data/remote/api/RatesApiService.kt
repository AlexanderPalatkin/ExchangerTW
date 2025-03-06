package com.example.exchangertw.data.remote.api

import com.example.exchangertw.data.remote.model.ExchangeRatesResponse
import com.example.exchangertw.data.remote.utils.NetworkParams
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface RatesApiService {
    @GET(NetworkParams.LATEST_RATES_ENDPOINT)
    fun getLatestRates(): Single<ExchangeRatesResponse>
}