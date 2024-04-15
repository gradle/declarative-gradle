package com.example

import com.google.common.collect.ImmutableList

class MyApp {
    fun getBuffer(): MyBuffer {
        val bufferMaker = getBufferMaker() // Extension function defined in debug and release source
        val buffer = bufferMaker.getBuffer()
        println("Made a buffer with size: ${buffer.size}")
        return buffer
    }

    // Demonstrates Guava is part of API
    fun getAbcs() = ImmutableList.of("a", "b", "c")

    // Demonstrate Project dependency
    fun getWelcome() = Utils().getWelcome()
}

