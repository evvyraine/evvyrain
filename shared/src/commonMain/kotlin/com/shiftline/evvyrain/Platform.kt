package com.shiftline.evvyrain

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform