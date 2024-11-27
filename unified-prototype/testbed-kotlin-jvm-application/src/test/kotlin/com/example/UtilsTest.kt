package com.example

import com.example.utils.Utils
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class UtilsTest {
    @Test
    fun testWelcome() {
        val utils = Utils()
        assertEquals("Welcome to the kotlin-jvm-utils library!", utils.welcome)
    }
}
