package com.example.exchangertw.utils

import android.content.Context
import com.example.exchangertw.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StringResourceProvider @Inject constructor(
    private val context: Context
) {
    fun getError(): String = context.getString(R.string.error)
    fun getNetworkError(): String = context.getString(R.string.network_error)
    fun getServerError(): String = context.getString(R.string.server_error)
    fun getUnknownError(): String = context.getString(R.string.unknown_error)
    fun getUpdatingDataError(): String = context.getString(R.string.updating_data_error)

    fun getDollarSign(): String = context.getString(R.string.sign_dollar)
    fun getEuroSign(): String = context.getString(R.string.sign_euro)
    fun getPoundSign(): String = context.getString(R.string.sign_pound)

    fun getDoubleZero(): String = context.getString(R.string.double_zero)
    fun getHaveNoMoney(): String = context.getString(R.string.have_no_money)
    fun getOk(): String = context.getString(R.string.ok)
    fun getSuccessful(): String = context.getString(R.string.successful)

    fun getSell(): String = context.getString(R.string.sell)
    fun getBuy(): String = context.getString(R.string.buy)

    fun getOne(): String = context.getString(R.string.one)
}