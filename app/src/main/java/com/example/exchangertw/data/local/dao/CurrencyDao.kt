package com.example.exchangertw.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.exchangertw.data.local.model.CurrencyEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateCurrency(currency: CurrencyEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateCurrencies(currencies: List<CurrencyEntity>): Completable

    @Query("SELECT * FROM currencies")
    fun getAllCurrencies(): Flowable<List<CurrencyEntity>>

    @Query("SELECT * FROM currencies WHERE code = :code")
    fun getCurrencyByCode(code: String): Single<CurrencyEntity>
}
