package com.example.exchangertw.presentation.exchanger.state

import com.example.exchangertw.domain.model.Currency

sealed class ExchangerState {
    data class Data(val data: List<Currency>) : ExchangerState()
    data object Loading : ExchangerState()
    class Error(val message: String) : ExchangerState()
    data object Start : ExchangerState()
}