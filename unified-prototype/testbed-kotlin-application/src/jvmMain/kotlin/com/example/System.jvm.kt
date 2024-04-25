package com.example

actual fun system(): String {
    return "JVM ${System.getProperty("java.version")}"
}