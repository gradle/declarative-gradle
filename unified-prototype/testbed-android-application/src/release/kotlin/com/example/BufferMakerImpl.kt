package com.example

import okio.Buffer

// BufferMaker release implementation using OKIO
class BufferMakerOKIOImpl() : BufferMaker {
    private val buffer: Buffer = okio.Buffer()

    override fun getBuffer(): MyBuffer {
        return MyBufferOKIOImpl(buffer)
    }
}

class MyBufferOKIOImpl(private val buffer: Buffer) : MyBuffer {
    override val size: Long
        get() = buffer.size
}

fun MyApp.getBufferMaker(): BufferMaker {
    return BufferMakerOKIOImpl()
}
