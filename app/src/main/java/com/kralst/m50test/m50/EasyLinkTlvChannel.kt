package com.kralst.m50test.m50

import com.kralst.m50test.utils.Tools
import com.kralst.m50test.card.tlv.Tlv
import com.kralst.m50test.card.tlv.TlvChannel
import com.kralst.m50test.card.tlv.encodeToByteArray
import com.kralst.m50test.card.tlv.encodeToString
import com.paxsz.easylink.model.DataModel
import timber.log.Timber
import java.io.ByteArrayOutputStream

private inline val log get() = Timber.tag("ESC")

/**
 * Uses the given [EasyLinkSdk] to implement the [TlvChannel] interface.
 */
class EasyLinkTlvChannel(
    /**
     * Used to set TLVs
     */
    private val easyLinkSdk: EasyLinkSdk,

    /**
     * TLV data type (either EMV or EasyLink)
     */
    private val dataType: DataModel.DataType
) : TlvChannel {
    override fun getTlv(tag: String): ByteArray? {
        val outputStream = ByteArrayOutputStream()
        val ret = easyLinkSdk.use {
            val tagBytes = Tools.str2Bcd(tag)

            getData(dataType, tagBytes, outputStream)
        }
        return if (ret == EasyLinkConstants.RESULT_SUCCESS) {
            val data = outputStream.toByteArray()

            val tagNameLengthBytes = tag.length / 2

            val apduResponseLengthBytes = 2

            // calculate the index at which the TLV's length is present
            val tlvLengthIndex = apduResponseLengthBytes + tagNameLengthBytes

            val headerLength =
                tlvLengthIndex + 1 // add 1 byte to include the byte holding the TLV's value length

            val specifiedLength = data[tlvLengthIndex].toInt()

            // verify that the specified TLV length is equal
            // to the length of the actual value retrieved
            require(data.size - headerLength == specifiedLength) {
                log.e(
                    """
                    tag=$tag
                    headerLength=${tlvLengthIndex}
                    data=${Tools.bcd2Str(data)}
                    data.size=${data.size}
                    len=${specifiedLength}
                """.trimIndent()
                )
                "Retrieved TLV is of invalid length"
            }

            // return TLV's value
            return data.copyOfRange(headerLength, data.size)
        } else {
            log.e("Failed to get tlv by tag=$tag, ret=$ret")
            null
        }
    }

    override fun setTlv(tlv: Tlv) = easyLinkSdk.use {
        val resultData = ByteArrayOutputStream()
        setData(
            dataType,
            tlv.encodeToByteArray(),
            resultData
        )
            .verify()
            .also { success ->
                if (!success) {
                    log.d("resultData for ${tlv.name} is ${Tools.bcd2Str(resultData.toByteArray())}")
                }
            }
    }

    override fun setTlvs(tlvs: List<Tlv>) = easyLinkSdk.use {
        val resultData = ByteArrayOutputStream()
        setData(
            dataType,
            tlvs.encodeToByteArray(),
            resultData
        )
            .verify()
            .also { success ->
                if (!success) {
                    log.d("resultData for ${tlvs.encodeToString()} is ${Tools.bcd2Str(resultData.toByteArray())}")
                }
            }
    }

    private fun Int.verify() = this == EasyLinkConstants.RESULT_SUCCESS
}

