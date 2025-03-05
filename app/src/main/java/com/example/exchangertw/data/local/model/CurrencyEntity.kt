package com.example.exchangertw.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class CurrencyEntity(
    @PrimaryKey val code: String,
    val rate: Double,
    var amount: Double
)
