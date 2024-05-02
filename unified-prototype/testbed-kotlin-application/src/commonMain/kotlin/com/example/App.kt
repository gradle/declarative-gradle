package com.example

import com.example.utils.Utils

fun main() {
    val messages = listOf(
        "Hello from Kotlin ${KotlinVersion.CURRENT} on ${platform().name}",
        Utils().welcome,
        daysPhrase()
    )

    messages.forEach(::println)
}