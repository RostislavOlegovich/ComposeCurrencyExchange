package com.rostyslavhrebeniuk.currencyexchanger.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rostyslavhrebeniuk.currencyexchanger.domain.Balance
import com.rostyslavhrebeniuk.currencyexchanger.domain.Currency
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : UserDataRepository {

    companion object {
        private const val FREE_CONVERSIONS_PREF = "free_conversions"
        private const val BALANCE_PREF = "initial_balance"
    }

    override fun getBalances(): List<Balance> {
        val gson = Gson()
        val json = sharedPreferences.getString(BALANCE_PREF, "")

        if (json.isNullOrBlank()) {
            return listOf(
                Balance(value = "1000.0", currency = Currency.EUR.name),
                Balance(value = "0.0", currency = Currency.USD.name)
            )
        }

        val type = object : TypeToken<List<Balance>>() {}.type
        return gson.fromJson(json, type)
    }

    override fun updateBalances(balances: List<Balance>) {
        val gson = Gson()
        val json = gson.toJson(balances)

        sharedPreferences.edit().putString(BALANCE_PREF, json).apply()
    }

    override fun getFreeConversions() = sharedPreferences.getInt(FREE_CONVERSIONS_PREF, 5)

    override fun updateFreeConversions() {
        val freeConversions = getFreeConversions()
        if (freeConversions == 0) return
        sharedPreferences.edit().putInt(FREE_CONVERSIONS_PREF, freeConversions - 1).apply()
    }
}