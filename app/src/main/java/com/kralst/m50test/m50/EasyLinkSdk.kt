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
    context: Context
) {
    private val easyLinkSdkManager = EasyLinkSdkManager.getInstance(context)

    val neptuneDal = NeptuneLiteUser.getInstance().getDal(context)

    // TODO support other models than M50
    fun connect() = connectOnM50()

    private fun connectOnM50(): Boolean {
        // it is required to wake up the R20 before connecting
        neptuneDal.paymentDevice.wakeup()

        // setup IComm implementation for R20
        // (taken from the EasyLink SDK sample application)
        val iComm = neptuneDal.commManager.getUartComm(
            UartParam().apply {
                port = EUartPort.COM19
            }
        )

        val uartComm = UartComm(iComm)

        val connectTimeoutMillis = 10 * 1000
        return easyLinkSdkManager.connect(
            uartComm,
            connectTimeoutMillis
        ) == EasyLinkConstants.RESULT_CONNECTED
    }

    fun <T> use(action: EasyLinkSdkManager.() -> T) =
        easyLinkSdkManager.action()

    suspend fun <T> useSuspend(action: suspend EasyLinkSdkManager.() -> T) =
        easyLinkSdkManager.action()
}