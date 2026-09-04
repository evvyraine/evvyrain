package com.shiftline.evvyrain

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.window
import web.http.byteArray
import web.http.fetch
import web.http.text

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        App(
            openUrl = { url -> window.open(url, "_blank", "noopener,noreferrer") },
            loadTextAsset = { path ->
                fetch(path).takeIf { it.ok }?.text()
            },
            loadBinaryAsset = { path ->
                fetch(path).takeIf { it.ok }?.byteArray()
            },
        )
    }
}
