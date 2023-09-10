package com.kralst.m50test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.kralst.m50test.card.CardReader
import com.kralst.m50test.m50.EasyLinkCardReader
import com.kralst.m50test.m50.EasyLinkSdk
import com.kralst.m50test.ui.theme.M50TestTheme
import com.paxsz.easylink.api.EasyLinkSdkManager
import com.paxsz.easylink.model.DataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date

private inline val log get() = Timber.tag("main-activity")

class MainActivity : ComponentActivity() {

    private val easyLinkSdk by lazy { EasyLinkSdk(this) }

    private val cardReader: CardReader by lazy {
        EasyLinkCardReader(
            easyLinkSdk = easyLinkSdk
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            M50TestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Column {

                        Greeting("Android")
                        Button(onClick = {
                            readCard()
                        }) {
                            Text("Start reading card")
                        }
                    }
                }
            }
        }
    }

    private fun readCard() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                runCatching {

                    val connected = easyLinkSdk.connect()

                    if (!connected) {
                        log.e("EasyLink failed to connect")
                        return@runCatching
                    }

                    val cardReaderResult = cardReader.startSaleTransaction(
                        amount = BigDecimal("10.30"),
                        date = Date()
                    )

                    log.d("cardReaderResult = $cardReaderResult")

                }.onFailure {
                    log.e(it)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    M50TestTheme {
        Greeting("Android")
    }
}