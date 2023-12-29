package com.rostyslavhrebeniuk.currencyexchanger.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rostyslavhrebeniuk.currencyexchanger.R
import com.rostyslavhrebeniuk.currencyexchanger.domain.Currency
import com.rostyslavhrebeniuk.currencyexchanger.ui.CurrencyExchangeCompose
import com.rostyslavhrebeniuk.currencyexchanger.ui.dialog.AlertDialogResult
import com.rostyslavhrebeniuk.currencyexchanger.ui.theme.CurrencyExchangerTheme
import com.rostyslavhrebeniuk.currencyexchanger.utils.getNames
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyExchangerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val mainViewModel: MainViewModel = hiltViewModel()

    var openDialog by remember { mutableStateOf(false) }
    val conversionResult by mainViewModel.conversionResult.collectAsStateWithLifecycle()
    val freeConversions by mainViewModel.freeConversions.collectAsStateWithLifecycle()
    val submitEnabled by mainViewModel.submitEnabled.collectAsStateWithLifecycle()
    val sellValue by mainViewModel.sellValue.collectAsStateWithLifecycle()
    val receiveValue by mainViewModel.receiveValue.collectAsStateWithLifecycle()
    val balances = remember { mainViewModel.balances }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                title = {
                    Text(text = stringResource(id = R.string.main_screen_title))
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
            ,
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(id = R.string.my_balances).uppercase()
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(balances) { item ->
                    Text(
                        modifier = Modifier.padding(end = 24.dp),
                        text = "${item.value} ${item.currency}",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(id = R.string.currency_exchange).uppercase()
            )

            CurrencyExchangeCompose(
                iconId = R.drawable.arrow_up,
                iconBackgroundColor = Color.Red,
                titleId = R.string.sell,
                value = sellValue,
                currencies = listOf(Currency.EUR.name),
                selectedCurrency = {
                    mainViewModel.selectSellCurrency(it)
                },
                onCurrencyValueChange = { value ->
                    mainViewModel.updateSellValue(value)
                    mainViewModel.exchange(value)
                }
            )

            Divider(
                modifier = Modifier
                    .padding(horizontal = 36.dp, vertical = 8.dp)
                    .height(1.dp)
                    .background(Color.LightGray)
            )

            CurrencyExchangeCompose(
                iconId = R.drawable.arrow_down,
                iconBackgroundColor = Color.Green,
                titleId = R.string.receive,
                value = receiveValue,
                readOnly = true,
                currencies = getNames<Currency>(),
                selectedCurrency = {
                    mainViewModel.selectReceiveCurrency(it)
                }
            )

            Button(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                onClick = {
                    mainViewModel.submitExchange()
                    openDialog = true
                },
                enabled = submitEnabled
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = stringResource(id = R.string.submit).uppercase()
                )
            }

            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.free_conversions, freeConversions)
            )

            if (openDialog) {
                conversionResult?.let { result ->
                    AlertDialogResult(result) {
                        openDialog = false
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    CurrencyExchangerTheme {
        MainScreen()
    }
}