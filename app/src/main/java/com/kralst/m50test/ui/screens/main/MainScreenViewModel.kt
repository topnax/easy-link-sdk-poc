package com.kralst.m50test.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kralst.m50test.card.CardReader
import com.kralst.m50test.card.CardReaderResult
import com.kralst.m50test.card.MifareMCardReader
import com.kralst.m50test.m50.EasyLinkSdk
import com.kralst.m50test.ui.screens.main.MainScreenContract.Event
import com.kralst.m50test.ui.screens.main.MainScreenContract.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.math.BigDecimal
import java.util.Date

class MainScreenViewModel(
    private val easyLinkSdk: EasyLinkSdk,
    private val cardReader: CardReader,
    private val mifareMCardReader: MifareMCardReader,
) : ViewModel() {

    private var _state = MutableStateFlow(State())

    val state: StateFlow<State> = _state

    fun onEvent(event: Event) {
        Timber.d("Incoming event: $event")
        when (event) {
            is Event.AmountChanged -> onAmountChanged(event)
            Event.ErrorsDismissed -> onErrorsDismissed()
            is Event.TransactionStartRequested -> onTransactionStartRequested(event)
            is Event.MifareMCardReadRequested -> onMifareMReadRequested(event)
            Event.CardReadResultDismissed -> onCardReadResultDismissed()
        }
        Timber.d("State after event processing: ${state.value}")
    }

    private fun onMifareMReadRequested(event: Event) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _state.update {
                    it.copy(
                        transactionInProgress = true
                    )
                }

                if (!easyLinkSdk.connect()) {
                    processCardReadError("Couldn't connect to the SDK.")
                    return@withContext
                }

                val result = mifareMCardReader.read()

                _state.update {
                    it.copy(
                        transactionInProgress = false
                    )
                }
            }
        }
    }

    private fun onCardReadResultDismissed() {
        _state.value = _state.value.copy(
            cardReadResult = null
        )
    }

    private fun onTransactionStartRequested(event: Event.TransactionStartRequested) {
        parseTransactionAmount(event.amountInput)
            ?.let { parsedAmount ->
                // amount parsed:
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        _state.update {
                            it.copy(
                                transactionInProgress = true
                            )
                        }

                        if (!easyLinkSdk.connect()) {
                            processCardReadError("Couldn't connect to the SDK.")
                            return@withContext
                        }

                        val result = cardReader.startSaleTransaction(
                            amount = parsedAmount,
                            date = Date()
                        )

                        when (result) {
                            is CardReaderResult.CardRead -> processCardReadResult(result)
                            is CardReaderResult.Error -> processCardReadError(result.exception.message)
                            CardReaderResult.Timeout -> processCardReadError("Card read timeout")
                        }
                    }
                }
            }
    }

    private fun processCardReadError(message: String?) {
        _state.update {
            it.copy(
                transactionInProgress = false,
                errorPrompt = message ?: "Unknown error",
                cardReadResult = null
            )
        }
    }

    private fun processCardReadResult(result: CardReaderResult.CardRead) {
        _state.update {
            it.copy(
                transactionInProgress = false,
                cardReadResult = result
            )
        }
    }

    private fun onErrorsDismissed() {
        _state.update {
            it.copy(
                errorPrompt = null
            )
        }
    }

    private fun onAmountChanged(event: Event.AmountChanged) {
        parseTransactionAmount(event.amount)
            ?.let {
                _state.update {
                    it.copy(
                        amountInput = event.amount
                    )
                }
            }
    }

    // TODO extend validation
    private fun parseTransactionAmount(amountInput: String) =
        amountInput.replace(",", ".").runCatching {
            BigDecimal(this)
        }
            .onFailure {
                Timber.w(it, "$amountInput can't be parsed")
            }
            .getOrNull()
}