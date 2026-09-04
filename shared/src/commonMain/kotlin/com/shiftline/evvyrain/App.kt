package com.shiftline.evvyrain

import androidx.compose.runtime.Composable

@Composable
fun App(
    openUrl: (String) -> Unit = {},
    loadTextAsset: suspend (String) -> String? = { null },
    loadBinaryAsset: suspend (String) -> ByteArray? = { null },
) = PortfolioAppV2(openUrl, loadTextAsset, loadBinaryAsset)
