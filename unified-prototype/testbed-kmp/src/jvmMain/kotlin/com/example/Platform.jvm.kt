package com.example

import org.apache.commons.lang3.StringUtils

class JVMSPlatform : Platform {
    override var name: String = "jVM"
}

actual fun getPlatform(): Platform {
    val platform = JVMSPlatform()
    // Testing a JVM dep here
    platform.name = StringUtils.capitalize(platform.name)
    return platform
}
