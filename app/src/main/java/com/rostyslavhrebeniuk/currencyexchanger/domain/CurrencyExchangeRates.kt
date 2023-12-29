package com.rostyslavhrebeniuk.currencyexchanger.domain

data class CurrencyExchangeRates(
    val base: String,
    val date: String,
    val rates: Rates
)
