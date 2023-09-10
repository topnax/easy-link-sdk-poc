package com.kralst.m50test.m50.card

import com.kralst.m50test.card.CardDataParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class CardDataParserTest {
    private val parser = CardDataParser()

    @Test
    fun `correctly parses track2 into CardData`() {
        val data = parser.parseFromTrack2(""";5581123456781323=160710212423468?""")

        assertNotNull(data)
        requireNotNull(data)

        assertEquals("5581123456781323", data.pan)
        assertEquals("1607", data.expiryDate)
        assertEquals("102", data.serviceCode)
        assertEquals("12423468", data.discretionaryData)
    }
}