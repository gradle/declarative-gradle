package com.example

fun getGreeting(): String {
    return "Hello from ${getPlatform().name}! ${daysPhrase()}"
}
