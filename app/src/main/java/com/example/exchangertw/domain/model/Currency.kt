package com.example.exchangertw.domain.model

data class Currency(
    val code: CurrencyCode,
    var rate: Double,
    var amount: Double
)

enum class CurrencyCode {
    AUD, GBP, USD, EUR
}
