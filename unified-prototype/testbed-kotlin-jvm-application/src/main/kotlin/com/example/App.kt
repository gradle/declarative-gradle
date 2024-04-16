package com.example

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap

fun main() {
    // Verify that Guava is available
    val values: ListMultimap<String, Long> = ArrayListMultimap.create()

    println("Hello from Kotlin on JVM ${System.getProperty("java.version")}")
}