package com.example

import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
actual fun platform(): Platform {
    return object : Platform {
        override val name: String
            get() {
                val platform = kotlin.native.Platform
                return "${platform.osFamily} ${platform.cpuArchitecture}"
            }
    }
}