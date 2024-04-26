package com.example

import com.example.utils.Utils

fun getGreeting(): List<String> {
    return listOf(
        "Hello from Kotlin ${KotlinVersion.CURRENT} on ${platform().name}",
        Utils().welcome,
        daysPhrase()
    )
}
