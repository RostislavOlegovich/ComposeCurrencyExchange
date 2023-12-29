package com.rostyslavhrebeniuk.currencyexchanger.ui.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.rostyslavhrebeniuk.currencyexchanger.domain.Currency
import com.rostyslavhrebeniuk.currencyexchanger.ui.textFieldDefaults
import com.rostyslavhrebeniuk.currencyexchanger.utils.getNames

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.Dropdown(items: List<String>, selectedCurrency: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(items.first()) }

    ExposedDropdownMenuBox(
        modifier = Modifier.wrapContentWidth().weight(1f),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            value = selectedOption,
            onValueChange = { },
            readOnly = true,
            modifier = Modifier.menuAnchor(),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
            colors = textFieldDefaults()
        )

        ExposedDropdownMenu(
            modifier = Modifier
                .background(Color.White)
                .exposedDropdownSize(),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEach {
                DropdownMenuItem(
                    text = {
                        Text(it)
                    },
                    onClick = {
                        selectedOption = it
                        selectedCurrency(it)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExposedDropdownPreview() {
    Row {
        Dropdown(getNames<Currency>()) {}
    }
}