package com.example.exchangertw.domain.model

sealed class ResultState<T, E> {
    data class Success<T, E>(val data: T) : ResultState<T, E>()
    data class Error<T, E>(val error: E) : ResultState<T, E>()
}