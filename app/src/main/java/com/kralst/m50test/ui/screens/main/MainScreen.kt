package com.kralst.m50test.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kralst.m50test.card.CardReaderResult
import com.kralst.m50test.ui.screens.main.MainScreenContract.Event
import com.kralst.m50test.ui.screens.main.MainScreenContract.State

@Composable
fun MainScreen(
    state: State,
    onEvent: (Event) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        state.errorPrompt?.also { errorPrompt ->
            ErrorPromptDialog(onEvent, errorPrompt)
        }

        state.cardReadResult?.also { cardReadResult ->
            CardReadResultDialog(
                cardRead = cardReadResult,
                onEvent = onEvent
            )
        }

        Text("PAX M50 Test", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (state.transactionInProgress) {
                CircularProgressIndicator()
            } else {
                TransactionSetupSection(state, onEvent)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TransactionSetupSection(
    state: State,
    onEvent: (Event) -> Unit
) {
    TextField(
        value = state.amountInput, onValueChange = {
            onEvent(Event.AmountChanged(amount = it))
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        label = {
            Text("Amount")
        },
        keyboardActions = KeyboardActions(onDone = {
            onEvent(Event.TransactionStartRequested(amountInput = state.amountInput))
        })
    )

    Button(onClick = {
        onEvent(
            Event.TransactionStartRequested(
                amountInput = state.amountInput
            )
        )
    }) {
        Text(
            "Start transaction"
        )
    }
}

@Composable
private fun CardReadResultDialog(cardRead: CardReaderResult.CardRead, onEvent: (Event) -> Unit) {
    Dialog(onDismissRequest = {
        onEvent(Event.CardReadResultDismissed)
    }) {
        Surface {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Card read:", style = MaterialTheme.typography.headlineSmall)

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val items = remember(cardRead) {
                        mapOf(
                            "PAN" to cardRead.data.pan.chunked(4).joinToString (separator = " "),
                            "Expiry date" to cardRead.data.expiryDate,
                            "Service code" to cardRead.data.serviceCode,
                            "Presentation method" to cardRead.presentationType.name,
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        items.forEach { (label, _) ->
                            Text(
                                "$label:",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column {
                        items.forEach { (_, value) ->
                            Text(
                                value,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Button(onClick = {
                    onEvent(Event.CardReadResultDismissed)
                }) {
                    Text("Dismiss")
                }
            }
        }
    }
}

@Composable
private fun ErrorPromptDialog(
    onEvent: (Event) -> Unit,
    errorPrompt: String
) {
    Dialog(onDismissRequest = { onEvent(Event.ErrorsDismissed) }) {
        Surface {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Error", style = MaterialTheme.typography.headlineSmall)
                Text(errorPrompt, style = MaterialTheme.typography.bodySmall)
                Button(onClick = {
                    onEvent(Event.ErrorsDismissed)
                }) {
                    Text("Dismiss")
                }
            }
        }

    }
}
