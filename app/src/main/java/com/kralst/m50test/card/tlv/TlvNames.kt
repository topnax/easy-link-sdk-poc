package com.kralst.m50test.card.tlv

/**
 * Names of commonly used TLVs in the world
 * of card transactions.
 */
object TlvNames {
    const val pan = "5A"
    const val track2 = "57"
    const val transactionType = "9C"
    const val transactionDate = "9A"
    const val transactionTime = "9F21"
    const val amount = "9F02"
    const val applicationCryptogram = "9F26"
    const val unpredictableNumber = "9F37"
}