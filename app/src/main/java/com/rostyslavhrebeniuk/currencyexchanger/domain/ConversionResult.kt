package com.rostyslavhrebeniuk.currencyexchanger.domain

data class ConversionResult(
    val sellValue: String,
    val receiveValue: String,
    val commissionValue: String
)
