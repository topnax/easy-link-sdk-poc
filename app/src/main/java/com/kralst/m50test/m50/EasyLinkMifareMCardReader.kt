package com.kralst.m50test.m50

import com.kralst.m50test.card.MifareMCardReader
import com.kralst.m50test.utils.Tools
import com.paxsz.easylink.model.picc.EDetectMode
import com.paxsz.easylink.model.picc.ELedStatus
import com.paxsz.easylink.model.picc.PiccCardInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

private inline val log get() = Timber.tag("EL-MCard")

class EasyLinkMifareMCardReader(
    private val easyLinkSdk: EasyLinkSdk
) : MifareMCardReader {

    override fun read() {
        val cardInfo = PiccCardInfo()
        easyLinkSdk.use {
            setDebugMode(true)

            val opened = piccOpen() == EasyLinkConstants.RESULT_SUCCESS

            if (!opened) {
                return@use
            }

            piccLight(0x02.toByte(), ELedStatus.ON)

            val read = runCatching {
                runBlocking {
                    withTimeout(10.seconds) {
                        while (isActive) {
                            val ret = piccDetect(EDetectMode.ONLY_M, cardInfo);
                            if (ret == 0) {
                                // card detected
                                log.d(
                                    "piccDetect getSerialInfo:%s",
                                    Tools.bcd2Str(cardInfo.serialInfo)
                                );
                                log.d("piccDetect getCardType:%s", cardInfo.cardType)
                                log.d("piccDetect getOther:%s", Tools.bcd2Str(cardInfo.other))
                                break;
                            }
                            delay(500)
                        }
                        true
                    }
                }
            }.getOrDefault(false)

            // // TODO setup authority
            //  val keyType = if ("A" == password) {
            //      EM1KeyType.TYPE_A
            //  } else {
            //      EM1KeyType.TYPE_A
            //  }

            // for (block in 0 until 256) {
            //     val authority = piccM1Authority(
            //         keyType,
            //         block.toByte(),
            //         Tools.str2Bcd(password),
            //         cardInfo.serialInfo
            //     )
            //     log.d("authority: $authority")
            // }

            // // TODO read data
            // for (block in 0 until 256) {
            //     val byteArray = ByteArray(16)
            //     val ret = piccM1ReadBlock(block.toByte(), byteArray)
            //     log.d("$ret, read block $block: ${Tools.bcd2Str(byteArray)}")
            // }
        }
    }
}