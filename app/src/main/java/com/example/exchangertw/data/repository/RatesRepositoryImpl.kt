package com.example.exchangertw.data.repository

import com.example.exchangertw.data.local.dao.CurrencyDao
import com.example.exchangertw.data.local.model.CurrencyEntity
import com.example.exchangertw.data.remote.api.RatesApiService
import com.example.exchangertw.domain.model.Currency
import com.example.exchangertw.domain.model.CurrencyCode
import com.example.exchangertw.domain.model.ErrorType
import com.example.exchangertw.domain.model.ResultState
import com.example.exchangertw.domain.repository.RatesRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RatesRepositoryImpl @Inject constructor(
    private val apiService: RatesApiService,
    private val currencyDao: CurrencyDao
) : RatesRepository {
    override fun getLatestRates(): Single<ResultState<List<Currency>, ErrorType>> {
        return apiService.getLatestRates()
            .flatMap { response ->
                currencyDao.getAllCurrencies().firstOrError()
                    .flatMap { savedCurrencies ->

                        val filteredRates = response.rates.filter { (code, _) ->
                            CurrencyCode.entries.any { it.name == code }
                        }

                        val updatedCurrencies = filteredRates.map { (code, rate) ->
                            val savedCurrency = savedCurrencies.find { it.code == code }
                            CurrencyEntity(
                                code = code,
                                rate = rate,
                                amount = savedCurrency?.amount ?: 100.0
                            )
                        }

                        currencyDao.insertOrUpdateCurrencies(updatedCurrencies)
                            .andThen(currencyDao.getAllCurrencies().firstOrError())
                            .map { currencies ->
                                val domainCurrencies = currencies.map { entity ->
                                    Currency(
                                        code = CurrencyCode.valueOf(entity.code),
                                        rate = entity.rate,
                                        amount = entity.amount
                                    )
                                }
                                ResultState.Success<List<Currency>, ErrorType>(domainCurrencies) as ResultState<List<Currency>, ErrorType>
                            }
                    }
            }
            .onErrorReturn { error ->
                val errorType = when (error) {
                    is IOException -> ErrorType.NETWORK_ERROR
                    is HttpException -> ErrorType.SERVER_ERROR
                    else -> ErrorType.UNKNOWN_ERROR
                }
                ResultState.Error(errorType)
            }
    }

    override fun updateCurrencyAmount(
        code: String,
        newAmount: Double,
        isSell: Boolean
    ): Completable {
        return currencyDao.getCurrencyByCode(code)
            .flatMapCompletable { currency ->
                if (isSell) {
                    currency.amount -= newAmount
                } else {
                    currency.amount += newAmount
                }
                currencyDao.insertOrUpdateCurrency(currency)
            }
    }
}