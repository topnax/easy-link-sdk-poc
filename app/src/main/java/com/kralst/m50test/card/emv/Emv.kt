package com.kralst.m50test.card.emv

import com.kralst.m50test.card.CardData
import java.math.BigDecimal
import java.util.Date

/**
 * Used to set EMV transaction data.
 *
 * Individual method calls may raise an [EmvException] when
 * an error occurs.
 */
interface Emv {

    /**
     * Sets initial data to be able to perform a transaction
     *
     * @param amount amount to be charged from the card
     * @param date the date at which the transaction was created
     */
    fun setSaleInitialTransactionData(
        amount: BigDecimal,
        date: Date
    )

    /**
     * Retrieves basic data from the card.
     *
     * @return [CardData] retrieved from the card.
     */
    fun getCardData(): CardData

    /**
     * Returns the unpredictable number to be used in an online authorization request.
     */
    fun getUnpredictableNumber(): Int

    /**
     * Returns the application cryptogram to be used in an online authorization request.
     */
    fun getApplicationCryptogram(): String

    class EmvException(
        exception: Exception
    ) : Exception(exception)
}