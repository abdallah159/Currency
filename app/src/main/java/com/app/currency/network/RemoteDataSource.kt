package com.app.currency.network

import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val service: Service) {

    suspend fun getConvertedRate(from: String, to: String, amount: Double) =
        service.convertCurrency(from = from, to = to, amount = amount)

    suspend fun getCurrencyHistory(date: String, from: String, to: String, amount: Double) =
        service.currencyHistory(date = date, from = from, to = to, amount = amount)

}