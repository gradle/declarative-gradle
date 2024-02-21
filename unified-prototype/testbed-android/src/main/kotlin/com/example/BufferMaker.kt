package com.example


interface BufferMaker {
    fun getBuffer(): MyBuffer
}

interface MyBuffer {
    val size: Long
}