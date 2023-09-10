package com.kralst.m50test.card.tlv

import com.kralst.m50test.utils.Tools

class Tlv(
    val name: String,
    val value: String
)

// TLV helper functions:

fun Tlv(name: String, valueBytes: ByteArray) = Tlv(
    name = name,
    value = Tools.bcd2Str(valueBytes)
)

fun Tlv.encodeToString() =
    "$name${Tools.bcd2Str(Tools.int2ByteArray(value.length / 2))}${value}"

fun Tlv.encodeToByteArray(): ByteArray =
    encodeToString().let {
        Tools.str2Bcd(it)
    }

fun List<Tlv>.encodeToString() =
    joinToString(separator = "") { it.encodeToString() }

fun List<Tlv>.encodeToByteArray(): ByteArray =
    joinToString(separator = "") {
        it.encodeToString()
    }
        .let {
            Tools.str2Bcd(it)
        }
