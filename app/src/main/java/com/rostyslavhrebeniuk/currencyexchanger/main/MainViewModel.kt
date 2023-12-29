package com.rostyslavhrebeniuk.currencyexchanger.main

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rostyslavhrebeniuk.currencyexchanger.data.CurrencyRepository
import com.rostyslavhrebeniuk.currencyexchanger.data.UserDataRepository
import com.rostyslavhrebeniuk.currencyexchanger.di.CurrencyRepositoryAnnotation
import com.rostyslavhrebeniuk.currencyexchanger.di.DefaultDispatcher
import com.rostyslavhrebeniuk.currencyexchanger.di.UserDataRepositoryAnnotation
import com.rostyslavhrebeniuk.currencyexchanger.domain.Balance
import com.rostyslavhrebeniuk.currencyexchanger.domain.ConversionResult
import com.rostyslavhrebeniuk.currencyexchanger.domain.Currency
import com.rostyslavhrebeniuk.currencyexchanger.domain.CurrencyExchangeRates
import com.rostyslavhrebeniuk.currencyexchanger.utils.asDouble
import com.rostyslavhrebeniuk.currencyexchanger.utils.readInstanceProperty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @CurrencyRepositoryAnnotation private val currencyRepository: CurrencyRepository,
    @UserDataRepositoryAnnotation private val userDataRepository: UserDataRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val decimalFormat = DecimalFormat("#.##").also {
        it.roundingMode = RoundingMode.UP
    }

    private val _commission = MutableStateFlow(0.0)

    private val _conversionResult = MutableStateFlow<ConversionResult?>(null)
    val conversionResult: StateFlow<ConversionResult?> = _conversionResult.asStateFlow()

    private val _currencyExchangeRate = MutableStateFlow<CurrencyExchangeRates?>(null)

    private val _balances = mutableStateListOf<Balance>()
    val balances: SnapshotStateList<Balance> = _balances

    private val _freeConversions = MutableStateFlow(0)
    val freeConversions: StateFlow<Int> = _freeConversions.asStateFlow()

    private val _submitEnabled = MutableStateFlow(false)
    val submitEnabled: StateFlow<Boolean> = _submitEnabled.asStateFlow()

    private val _sellValue = MutableStateFlow("")
    val sellValue: StateFlow<String> = _sellValue.asStateFlow()

    private val _receiveValue = MutableStateFlow("")
    val receiveValue: StateFlow<String> = _receiveValue.asStateFlow()

    private val _sellCurrency = MutableStateFlow(Currency.EUR)
    val sellCurrency: StateFlow<Currency> = _sellCurrency.asStateFlow()

    private val _receiveCurrency = MutableStateFlow(Currency.EUR)
    val receiveCurrency: StateFlow<Currency> = _receiveCurrency.asStateFlow()

    private val _remainder = MutableStateFlow("")

    init {
        getCurrencyExchangeRates()
        _freeConversions.value = userDataRepository.getFreeConversions()
        _balances.clear()
        _balances.addAll(userDataRepository.getBalances())
    }

    private fun getCurrencyExchangeRates() {
        try {
            viewModelScope.launch(defaultDispatcher) {
                _currencyExchangeRate.value = currencyRepository.getCurrencyExchangeRates()
            }
        } catch (e: Exception) {
            Log.e("MainViewModel", "Error in obtaining data on exchange rates")
        }
    }

    fun exchange(value: String) {
        if (value.isEmpty()) {
            _receiveValue.value = "0.0"
            return
        }
        val valueDouble = value.toDouble()
        _currencyExchangeRate.value?.let { rate ->
            val currencyRate: String = readInstanceProperty(rate.rates, receiveCurrency.value.name)
            val sellBalance = _balances.find { it.currency == sellCurrency.value.name } ?: return

            val commission = if (userDataRepository.getFreeConversions() > 0) {
                0.0
            } else {
                valueDouble * (COMMISSION_PERCENT / 100.0)
            }

            val remainder = sellBalance.value.asDouble() - valueDouble - commission

            _commission.value = commission

            _submitEnabled.value = remainder >= 0.00

            _remainder.value = decimalFormat.format(remainder)

            _receiveValue.value = decimalFormat.format(valueDouble * currencyRate.toDouble())
        }
    }

    fun submitExchange() {
        val sellBalance = _balances.find { it.currency == sellCurrency.value.name } ?: return
        var receiveBalance = _balances.find { it.currency == receiveCurrency.value.name }

        if (receiveBalance == null) {
            receiveBalance = Balance(
                value = "0",
                currency = receiveCurrency.value.name
            )
            _balances.add(receiveBalance)
        }

        _conversionResult.value = ConversionResult(
            sellValue = "${_sellValue.value} ${sellBalance.currency}",
            receiveValue = "${_receiveValue.value} ${receiveBalance.currency}",
            commissionValue = _commission.value.toString()
        )

        val updatedBalances = _balances.map { balance ->
            when (balance) {
                sellBalance -> {
                    val formatValue = decimalFormat.format(_remainder.value.asDouble())
                    balance.copy(value = formatValue)
                }

                receiveBalance -> {
                    val formatValue =
                        decimalFormat.format(balance.value.asDouble() + _receiveValue.value.asDouble())
                    balance.copy(value = formatValue)
                }

                else -> balance
            }
        }

        userDataRepository.updateFreeConversions()
        _freeConversions.value = userDataRepository.getFreeConversions()
        userDataRepository.updateBalances(updatedBalances)
        _balances.clear()
        _balances.addAll(userDataRepository.getBalances())
        clearValues()
    }

    fun selectSellCurrency(sellCurrency: String) {
        _sellCurrency.value = Currency.valueOf(sellCurrency)
        clearValues()
    }

    fun selectReceiveCurrency(receiveCurrency: String) {
        _receiveCurrency.value = Currency.valueOf(receiveCurrency)
        clearValues()
    }

    fun updateSellValue(sellValue: String) {
        _sellValue.value =
            if (sellValue.isEmpty()) "" else decimalFormat.format(sellValue.toDouble())
    }

    fun clearValues() {
        _sellValue.value = ""
        _receiveValue.value = "0.0"
    }

    companion object {
        private const val COMMISSION_PERCENT = 0.7
    }
}
