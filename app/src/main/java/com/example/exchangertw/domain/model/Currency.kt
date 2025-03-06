package com.example.exchangertw.domain.model

data class Currency(
    val code: CurrencyCode,
    var rate: Double,
    var amount: Double
) {
    fun calculateRate(otherCurrencyRate: Double): Double {
        return otherCurrencyRate / rate
    }
}

enum class CurrencyCode {
    AUD, GBP, USD, EUR
}
