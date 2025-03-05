package com.example.exchangertw.utils

import android.content.Context
import com.example.exchangertw.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StringResourceProvider @Inject constructor(
    private val context: Context
) {
    fun getNetworkError(): String = context.getString(R.string.network_error)
    fun getServerError(): String = context.getString(R.string.server_error)
    fun getUnknownError(): String = context.getString(R.string.unknown_error)
    fun getUpdatingDataError(): String = context.getString(R.string.updating_data_error)
}