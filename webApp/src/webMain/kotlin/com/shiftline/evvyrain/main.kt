package com.shiftline.evvyrain

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        App(openUrl = { url -> window.open(url, "_blank", "noopener,noreferrer") })
    }
}
