package com.example.exchangertw.presentation.exchanger.view_model

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.exchangertw.domain.model.ErrorType
import com.example.exchangertw.domain.model.ResultState
import com.example.exchangertw.domain.use_case.ExchangeUseCase
import com.example.exchangertw.presentation.exchanger.state.ExchangerState
import com.example.exchangertw.utils.StringResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ExchangerViewModel @Inject constructor(
    private val exchangeUseCase: ExchangeUseCase,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {

    private val _state = MutableLiveData<ExchangerState>(ExchangerState.Start)
    val state: LiveData<ExchangerState> get() = _state

    private val _sellText = MutableLiveData<String>()
    val sellText: LiveData<String> = _sellText

    private val _buyText = MutableLiveData<String>()
    val buyText: LiveData<String> = _buyText

    fun updateSellText(text: String) {
        _sellText.value = text
    }

    fun updateBuyText(text: String) {
        _buyText.value = text
    }

    private val disposable = CompositeDisposable()

    fun loadCurrencies() {
        disposable.add(
            Observable.interval(0, 30, TimeUnit.SECONDS)
                .flatMapSingle {
                    _state.postValue(ExchangerState.Loading)
                    exchangeUseCase()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ resultState ->
                    when (resultState) {
                        is ResultState.Success -> {
                            _state.value = ExchangerState.Data(resultState.data)
                        }

                        is ResultState.Error -> {
                            val errorMessage = when (resultState.error) {
                                ErrorType.NETWORK_ERROR -> stringResourceProvider.getNetworkError()
                                ErrorType.SERVER_ERROR -> stringResourceProvider.getServerError()
                                ErrorType.UNKNOWN_ERROR -> stringResourceProvider.getUnknownError()
                            }
                            _state.value = ExchangerState.Error(errorMessage)
                        }
                    }
                }, { error ->
                    error.printStackTrace()
                    _state.value = ExchangerState.Error(stringResourceProvider.getUnknownError())
                })
        )
    }

    @SuppressLint("CheckResult")
    fun updateCurrencyAmount(
        sellCode: String,
        buyCode: String,
        sellAmount: Double,
        buyAmount: Double
    ) {
        disposable.add(
            exchangeUseCase(sellCode, sellAmount, true) // isSell = true
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    loadCurrencies()
                }, {
                    _state.value =
                        ExchangerState.Error(stringResourceProvider.getUpdatingDataError())
                })
        )

        exchangeUseCase(buyCode, buyAmount, false)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                loadCurrencies()
            }, {
                _state.value = ExchangerState.Error(stringResourceProvider.getUpdatingDataError())
            })
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}