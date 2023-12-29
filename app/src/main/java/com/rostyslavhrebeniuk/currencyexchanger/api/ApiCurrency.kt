package com.rostyslavhrebeniuk.currencyexchanger.api

import com.rostyslavhrebeniuk.currencyexchanger.domain.CurrencyExchangeRates
import retrofit2.Response
import retrofit2.http.GET

interface ApiCurrency {

    @GET("api/currency-exchange-rates")
    suspend fun getCurrencyExchangeRates() : Response<CurrencyExchangeRates>
}