package com.kralst.m50test.card.tlv

/**
 * Names of commonly used TLVs in the world
 * of card transactions.
 */
object TlvNames {
    val pan = "5A"
    val track2 = "57"
    val transactionType = "9C"
    val transactionDate = "9A"
    val transactionTime = "9F21"
    val amount = "9F02"
    val applicationCryptogram = "9F26"
    val unpredictableNumber = "9F37"
}