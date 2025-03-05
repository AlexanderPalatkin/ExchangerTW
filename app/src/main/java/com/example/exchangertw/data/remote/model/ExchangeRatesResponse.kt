package com.example.exchangertw.data.remote.model

data class ExchangeRatesResponse(
    val base: String,
    val rates: Map<String, Double>
)
