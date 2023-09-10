package com.kralst.m50test.card.emv

import android.annotation.SuppressLint
import com.kralst.m50test.utils.Tools
import com.kralst.m50test.card.CardDataParser
import com.kralst.m50test.card.tlv.Tlv
import com.kralst.m50test.card.tlv.TlvChannel
import com.kralst.m50test.card.tlv.TlvNames
import timber.log.Timber
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date

private inline val log get() = Timber.tag("TLV-CHANNEL-EMV")

@SuppressLint("SimpleDateFormat")
private val simpleDateFormat = SimpleDateFormat("yyyyMMddHHmmss")

/**
 * An [Emv] implementation that uses a [TlvChannel]
 * to implement the interface.
 *
 * Translates high-level EMV actions to payloads/requests
 * of TLV tags.
 */
class TlvChannelEmv(
    private val tlvChannel: TlvChannel
) : Emv {

    private val cardDataParser = CardDataParser()

    override fun setSaleInitialTransactionData(amount: BigDecimal, date: Date) {
        // TODO support other transaction types, such as pre-authorization

        // TODO set:
        //  - merchant name
        //  - currency
        //  - country code
        //  - terminal capabilities
        //  - POS entry mode
        //  - ...

        // amount must be padded to 12 characters
        val amountPadded = amount.toTlvValue().padStart(length = 12, padChar = '0')

        val amountBcd = Tools.str2Bcd(amountPadded)

        val dateFormatted = simpleDateFormat.format(Date(System.currentTimeMillis()))

        log.d("date=$dateFormatted")

        // assemble essential EMV TLVs
        val tlvs = listOf(
            Tlv(TlvNames.transactionType, "00"), // normal purchase
            Tlv(TlvNames.transactionDate, dateFormatted.substring(2, 8)),
            Tlv(TlvNames.transactionTime, dateFormatted.substring(8, 14)),
            Tlv(TlvNames.amount, amountBcd), // amount
        )

        useTlvChannel {
            setTlvs(tlvs)
        }
    }

    override fun getCardData() =
        useTlvChannel {
            val track2Bytes =
                requireNotNull(getTlv(TlvNames.track2)) {
                    "Failed to get track 2"
                }

            val track2 = Tools.bcd2Str(track2Bytes)

            // parse CardData from retrieved track 2
            requireNotNull(cardDataParser.parseFromTrack2(track2)) {
                "Failed to parse track 2 data"
            }
        }

    override fun getUnpredictableNumber() = useTlvChannel {
        Tools.bytes2Int(
            requireNotNull(getTlv(TlvNames.unpredictableNumber).also {
                log.d("unp: ${Tools.bcd2Str(it)}")
            }) {
                "Unpredictable number unavailable"
            }
        )
    }

    override fun getApplicationCryptogram(): String = useTlvChannel {
        Tools.bcd2Str(
            requireNotNull(getTlv(TlvNames.applicationCryptogram)) {
                "Application cryptogram is unavailable"
            }
        )
    }

    private fun <T> useTlvChannel(action: TlvChannel.() -> T) =
        try {
            tlvChannel.action()
        } catch (e: Exception) {
            throw Emv.EmvException(e)
        }
}

private fun BigDecimal.toTlvValue() = movePointRight(2).toLong().toString()
