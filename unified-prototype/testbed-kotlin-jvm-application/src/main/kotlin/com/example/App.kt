package com.example

import com.example.utils.Utils
import com.google.common.collect.ImmutableList

fun main() {
    // Verify that Guava is available
    val builder = ImmutableList.builder<String>()
    builder.add("Hello from Kotlin on JVM ${System.getProperty("java.version")}")

    // Verify that the Kotlin JVM library is available
    val utils = Utils()
    builder.add(utils.welcome)

    val messages = builder.build()
    messages.forEach(::println)
}