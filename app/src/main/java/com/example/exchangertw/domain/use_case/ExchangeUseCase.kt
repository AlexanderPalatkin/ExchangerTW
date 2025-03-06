package com.example.exchangertw.domain.use_case

import com.example.exchangertw.domain.model.Currency
import com.example.exchangertw.domain.model.ErrorType
import com.example.exchangertw.domain.model.ResultState
import com.example.exchangertw.domain.repository.RatesRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ExchangeUseCase @Inject constructor(
    private val repository: RatesRepository
) {
    operator fun invoke(): Single<ResultState<List<Currency>, ErrorType>> {
        return repository.getLatestRates()
    }

    operator fun invoke(code: String, newAmount: Double, isSell: Boolean): Completable {
        return repository.updateCurrencyAmount(code, newAmount, isSell)
    }
}