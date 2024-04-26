package com.example

fun getGreeting(): String {
    return "Hello from ${platform().name}! ${daysPhrase()}"
}
