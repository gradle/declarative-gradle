package com.example

import com.squareup.sqldelight.db.SqlDriver

class JSPlatform : Platform {
    override val name: String = "JS"
}

actual fun platform(): Platform {
    // Just testing a JS dep here
    val driver: SqlDriver? = null

    if (driver != null) {
        println("Driver is not null")
    } else {
        println("Driver is null")
    }

    return JSPlatform()
}
