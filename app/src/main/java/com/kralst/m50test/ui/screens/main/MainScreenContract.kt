package com.kralst.m50test.ui.screens.main

import com.kralst.m50test.card.CardReaderResult

interface MainScreenContract {
    sealed interface Event {
        object MifareMCardReadRequested : Event

        data class TransactionStartRequested(
            val amountInput: String
        ) : Event

        object ErrorsDismissed : Event

        object CardReadResultDismissed : Event

        class AmountChanged(
            val amount: String
        ) : Event
    }

    data class State(
        val transactionInProgress: Boolean = false,
        val errorPrompt: String? = null,
        val cardReadResult: CardReaderResult.CardRead? = null,
        val amountInput: String = "2,34"
    )
}