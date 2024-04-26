package com.example

actual fun platform(): Platform {
    return object : Platform {
        override val name: String
            get() = "NodeJS"
    }
}