package com.kralst.m50test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.kralst.m50test.card.CardReader
import com.kralst.m50test.m50.EasyLinkCardReader
import com.kralst.m50test.m50.EasyLinkSdk
import com.kralst.m50test.ui.screens.main.MainScreen
import com.kralst.m50test.ui.screens.main.MainScreenViewModel
import com.kralst.m50test.ui.theme.M50TestTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {

    private val easyLinkSdk by lazy { EasyLinkSdk(this) }

    private val cardReader: CardReader by lazy {
        EasyLinkCardReader(
            easyLinkSdk = easyLinkSdk
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainScreenViewModel = MainScreenViewModel(easyLinkSdk, cardReader)
        setContent {
            M50TestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val state by mainScreenViewModel.state.collectAsState()
                    MainScreen(
                        state = state,
                        onEvent = mainScreenViewModel::onEvent
                    )
                }
            }
        }
    }
}
