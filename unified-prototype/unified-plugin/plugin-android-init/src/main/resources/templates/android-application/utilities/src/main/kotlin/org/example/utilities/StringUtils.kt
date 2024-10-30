package org.example.utilities

import org.example.list.LinkedList

object StringUtils {
    fun join(source: LinkedList): String {
        return JoinUtils.join(source)
    }

    fun split(source: String): LinkedList {
        return SplitUtils.split(source)
    }
}
