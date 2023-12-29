package com.rostyslavhrebeniuk.currencyexchanger.data

import com.rostyslavhrebeniuk.currencyexchanger.domain.CurrencyExchangeRates

interface CurrencyRepository {

    suspend fun getCurrencyExchangeRates(): CurrencyExchangeRates
}
