package com.kralst.m50test.card

private val track2Regex =
    """^;(?<PAN>[\d]{1,19}+)=(?<ED>[\d]{0,4}|=)(?<SC>[\d]{0,3}|=)(?<DD>.*)\?\Z""".toRegex()

/**
 * Used to parse [CardData] from a track 2.
 * Uses a track 2 regex to extract the data.
 */
class CardDataParser {
    fun parseFromTrack2(track2: String): CardData? =
        runCatching {
            val normalizedTrack2 = track2.toNormalizedTrack2()
            val result = requireNotNull(track2Regex.matchEntire(normalizedTrack2))

            val groups = result.groups as MatchNamedGroupCollection

            CardData(
                pan = groups.getGroupValue("PAN"),
                expiryDate = groups.getGroupValue("ED"),
                discretionaryData = groups.getGroupValue("DD"),
                serviceCode = groups.getGroupValue("SC"),
            )
        }
            .getOrNull()
}

/**
 * Normalizes the string to
 */
private fun String.toNormalizedTrack2(): String {
    // does it use different separator?
    if (matches("^[0-9]+D[0-9]+F?$".toRegex())) {
        var res: String = replace("D", "=") //TODO replacing the padding??
        if (res.matches(".*F$".toRegex())) {
            res = res.substring(0, res.length - 1)
        }
        return ";$res?"
    }

    // check sentinels
    return let {
        if (!startsWith(";")) {
            ";$it"
        } else {
            it
        }
    }
        .let {
           if (!endsWith("?")) {
               "${this}?"
           } else {
               it
           }
        }
}

private fun MatchNamedGroupCollection.getGroupValue(groupName: String) =
    requireNotNull(get(groupName)?.value)
