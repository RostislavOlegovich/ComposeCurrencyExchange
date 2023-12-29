package com.rostyslavhrebeniuk.currencyexchanger.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rostyslavhrebeniuk.currencyexchanger.R
import com.rostyslavhrebeniuk.currencyexchanger.domain.ConversionResult

@Composable
fun AlertDialogResult(
    conversionResult: ConversionResult,
    onClose: () -> Unit
) {
    AlertDialog(
        title = {
            Text(text = stringResource(id = R.string.currency_converted))
        },
        text = {
            Text(
                text = stringResource(
                    R.string.conversion_result,
                    conversionResult.sellValue,
                    conversionResult.receiveValue,
                    conversionResult.commissionValue
                )
            )
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = {
                    onClose()
                }
            ) {
                Text(stringResource(id = R.string.done))
            }
        }
    )
}