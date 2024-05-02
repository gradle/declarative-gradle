package com.example.lib

import com.example.utils.Utils
import com.google.common.collect.ImmutableList

class Library {
    val messages: Iterable<String>
        get() {
            // Verify that Guava is available
            val builder = ImmutableList.builder<String>()
            builder.add("Hello from Kotlin ${KotlinVersion.CURRENT} on JVM ${System.getProperty("java.version")}")

            // Verify that the Kotlin JVM library is available
            val utils = Utils()
            builder.add(utils.welcome)

            return builder.build()
        }
}