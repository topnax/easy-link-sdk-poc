package com.kralst.m50test.m50

import com.kralst.m50test.card.tlv.Tlv
import com.kralst.m50test.card.tlv.encodeToByteArray
import com.kralst.m50test.card.tlv.encodeToString
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class TlvTest {
    @ParameterizedTest
    @MethodSource("com.kralst.m50test.m50.TlvTestArguments#getTlvTestArguments")
    fun `single TLV is encoded to string correctly`(
        tlv: Tlv,
        expected: String
    ) {
        assertEquals(expected, tlv.encodeToString())
    }

    @Test
    fun `multiple TLVs are encoded correctly into a string`() {
        val tlvs = listOf(
            Tlv("5A", "102030"),
            Tlv("9F37", "8A6A"),
            Tlv("9A03", "100223")
        )

        val expected = "5A031020309F37028A6A9A0303100223"

        assertEquals(
            expected, tlvs.encodeToString()
        )
    }

    @Test
    fun `single TLV is encoded to byte array correctly`() {
        assertArrayEquals(
            byteArrayOf(
                0x9F.toByte(),
                0x37,
                0x03,
                0x5A,
                0x67,
                0xFC.toByte(),
            ),
            Tlv("9F37", "5A67FC").encodeToByteArray()
        )
    }
}

private object TlvTestArguments {
    @JvmStatic
    fun getTlvTestArguments(): List<Arguments> =
        listOf(
            Tlv("5A", "1020") to "5A021020",
            Tlv("9F37", "5A6F7C") to "9F37035A6F7C",
            Tlv("5A", byteArrayOf(0x10, 0x20)) to "5A021020"
        ).map {
            Arguments.of(
                it.first, it.second
            )
        }
}