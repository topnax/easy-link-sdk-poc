package com.kralst.m50test.m50

import com.kralst.m50test.card.CardReader
import com.kralst.m50test.card.CardReaderResult
import com.kralst.m50test.card.PresentationType
import com.kralst.m50test.card.emv.TlvChannelEmv
import com.paxsz.easylink.listener.IReportStatusListener
import com.paxsz.easylink.listener.ReportConstant
import com.paxsz.easylink.model.AppSelectResponse
import com.paxsz.easylink.model.DataModel
import com.paxsz.easylink.model.TLVDataObject
import com.paxsz.easylink.model.report.BaseReportedData
import com.paxsz.easylink.model.report.BaseRunCmdModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.math.BigDecimal
import java.util.Date

private inline val log get() = Timber.tag("EASY-LINK-CARD-READER")

class EasyLinkCardReader(
    private val easyLinkSdk: EasyLinkSdk
) : CardReader {

    private val emv = TlvChannelEmv(
        tlvChannel = EasyLinkTlvChannel(
            easyLinkSdk = easyLinkSdk,
            dataType = DataModel.DataType.TRANSACTION_DATA
        )
    )

    private val lastUsedPresentationType = MutableStateFlow<PresentationType?>(null)

    override suspend fun startSaleTransaction(
        amount: BigDecimal,
        date: Date
    ): CardReaderResult =
        easyLinkSdk.useSuspend {
            runCatching {
                registerReportStatusListener(getReportStatusListener(lastUsedPresentationType))

                // set transaction data
                emv.setSaleInitialTransactionData(amount, date)

                // clear the last used presentation method
                lastUsedPresentationType.value = null

                // start the transaction
                val ret = startTransaction()

                if (ret == EasyLinkConstants.RESULT_TIMEOUT) {
                    return@runCatching CardReaderResult.Timeout
                }

                check(ret == EasyLinkConstants.RESULT_SUCCESS) {
                    "startTransaction() failed: ret=$ret"
                }

                // wait for the used presentation method to become available
                val usedPresentationType = lastUsedPresentationType.filterNotNull().first()

                log.d("Transaction finished (usedPresentationMethod=$usedPresentationType)")

                val cardData = emv.getCardData()

                // log additional EMV data:
                log.d("unpredictableNumber=${emv.getUnpredictableNumber()}")
                log.d("applicationCryptogram=${emv.getApplicationCryptogram()}")

                CardReaderResult.CardRead(
                    data = cardData,
                    presentationType = usedPresentationType
                )
            }.getOrElse {
                CardReaderResult.Error(Exception(it))
            }
        }
}

// TODO implement all methods
//  - currently only onReadCard is utilized
private fun getReportStatusListener(presentationTypeStateFlow: MutableStateFlow<PresentationType?>): IReportStatusListener =
    object : IReportStatusListener {
        override fun onReportSearchMode(p0: String?, p1: Int) {
            log.d("onReportSearchMode($p0, $p1")
            // TODO implement
        }

        override fun onReadCard(prompts: String?, type: Int, readCardStatus: Int) {
            log.d("onReadCard($prompts, $type, $readCardStatus")
            // catch successful reads of types we're interested in
            if (readCardStatus == ReportConstant.STATUS_READ_CARD_SUCCESSFULLY) {
                val presentationType = when (type) {
                    ReportConstant.CONTACTLESS -> PresentationType.CLESS
                    ReportConstant.CONTACT -> PresentationType.CONTACT
                    ReportConstant.MAGNETIC -> PresentationType.MAGSTRIPE
                    else -> null
                } ?: return

                // update the state flow with the new presentation type
                presentationTypeStateFlow.value = presentationType
            }
        }

        override fun onSelectApp(
            p0: String?,
            p1: Int,
            p2: MutableList<String>?
        ): AppSelectResponse = AppSelectResponse()// TODO display candidate list

        override fun onEnterPin(p0: String?, p1: Int, p2: String?) {
            log.d("onEnterPin($p0, $p1, $p2")
            // TODO implement
        }

        override fun onSetParamToPinPad(
            p0: String?,
            p1: ArrayList<TLVDataObject>?,
            p2: ArrayList<TLVDataObject>?,
            p3: Int
        ): ArrayList<TLVDataObject> = arrayListOf() // TODO implement

        override fun onRunCmd(
            p0: ArrayList<out BaseReportedData>?,
            p1: Int
        ): ArrayList<BaseRunCmdModel> = arrayListOf() // TODO implement

    }
