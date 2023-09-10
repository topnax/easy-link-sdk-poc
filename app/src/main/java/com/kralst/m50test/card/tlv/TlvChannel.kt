package com.kralst.m50test.card.tlv

/**
 * Used to send/receive TLVs to/from a payment instrument.
 */
interface TlvChannel {
    fun getTlv(tag: String): ByteArray?
    fun setTlv(tlv: Tlv): Boolean
    fun setTlvs(tlvs: List<Tlv>): Boolean
}
