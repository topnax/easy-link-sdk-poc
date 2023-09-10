package com.kralst.m50test.card

import java.math.BigDecimal
import java.util.Date

interface CardReader {
    /**
     * Starts device's interfaces and waits for a card to be presented.
     * When a card is presented an attempt to perform a sale transaction
     * is made.
     *
     * @param amount The amount to be charged from the card.
     * @param date The date at which the transaction was initiated.
     *
     * @return A [CardReaderResult] instance containing the result.
     */
    suspend fun startSaleTransaction(
        amount: BigDecimal,
        date: Date
    ): CardReaderResult
}

/**
 * Represents a result of the attempt made to a read card
 * to facilitate a payment transaction.
 */
sealed interface CardReaderResult {
    /**
     * Card has been successfully read.
     */
    data class CardRead(
        val data: CardData,
        val presentationType: PresentationType
        // TODO include transaction details (such as data required for online authorization,
        //  CVM results, transaction counter...)
    ) : CardReaderResult

    /**
     * No payment instrument has been presented in time.
     */
    object Timeout : CardReaderResult

    /**
     * An unrecoverable error has occurred.
     */
    data class Error(val exception: Exception) : CardReaderResult
}

enum class PresentationType {
    CLESS,
    CONTACT,
    MAGSTRIPE
}

data class CardData(
    val pan: String,
    val expiryDate: String,
    val discretionaryData: String,
    val serviceCode: String
)