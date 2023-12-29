package com.rostyslavhrebeniuk.currencyexchanger.data

import com.rostyslavhrebeniuk.currencyexchanger.domain.Balance

interface UserDataRepository {

    fun getBalances(): List<Balance>

    fun updateBalances(balances: List<Balance>)

    fun getFreeConversions(): Int

    fun updateFreeConversions()
}
