package com.kralst.m50test.m50

import android.content.Context
import com.pax.dal.entity.EUartPort
import com.pax.dal.entity.UartParam
import com.pax.neptunelite.api.NeptuneLiteUser
import com.paxsz.easylink.api.EasyLinkSdkManager

object EasyLinkConstants {
    const val RESULT_TIMEOUT = 4008
    const val RESULT_CONNECTED = 1001
    const val RESULT_SUCCESS = 0
}

class EasyLinkSdk(
    private val context: Context
) {
    private val easyLinkSdkManager = EasyLinkSdkManager.getInstance(context)

    // TODO support other models than M50
    fun connect() = connectOnM50()

    private fun connectOnM50(): Boolean {
        // TODO doesn't work when an external connect request is not made

        // taken from the EasyLink SDK sample application:
        val uartParam = UartParam()
        uartParam.port = EUartPort.COM19
        val iComm =
            NeptuneLiteUser
                .getInstance()
                .getDal(context).commManager.getUartComm(uartParam)

        val uartComm = UartComm(iComm)

        val connectTimeoutMillis = 10 * 1000
        return easyLinkSdkManager.connect(uartComm, connectTimeoutMillis) == EasyLinkConstants.RESULT_CONNECTED
    }

    fun <T> use(action: EasyLinkSdkManager.() -> T)  =
       easyLinkSdkManager.action()

    suspend fun <T> useSuspend(action: suspend EasyLinkSdkManager.() -> T)  =
        easyLinkSdkManager.action()
}