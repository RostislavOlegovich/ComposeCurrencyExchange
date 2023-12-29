package com.rostyslavhrebeniuk.currencyexchanger.data

import com.rostyslavhrebeniuk.currencyexchanger.api.ApiCurrency
import com.rostyslavhrebeniuk.currencyexchanger.domain.CurrencyExchangeRates
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class CurrencyRepositoryImpl @Inject constructor(
    private val apiCurrency: ApiCurrency
) : CurrencyRepository {

    override suspend fun getCurrencyExchangeRates(): CurrencyExchangeRates {
        val response = apiCurrency.getCurrencyExchangeRates()
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw CancellationException()
        }
    }
}
