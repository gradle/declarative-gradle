package com.example

// BufferMaker test implementation doesn't use any external dependencies
class BufferMakerTestImpl() : BufferMaker {
    private val buffer: MyBuffer = MyBufferTestImpl()

    override fun getBuffer(): MyBuffer {
        return buffer
    }
}

class MyBufferTestImpl() : MyBuffer {
    private val buffer = listOf<String>()

    override val size: Long
        get() = buffer.size.toLong()
}

fun MyApp.getBufferMaker(): BufferMaker {
    return BufferMakerTestImpl()
}