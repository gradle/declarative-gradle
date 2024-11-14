package org.example.utilities

import org.example.list.LinkedList

internal object JoinUtils {
    fun join(source: LinkedList): String {
        val result: java.lang.StringBuilder = java.lang.StringBuilder()
        for (i in 0 until source.size()) {
            if (result.length > 0) {
                result.append(" ")
            }
            result.append(source.get(i))
        }

        return result.toString()
    }
}
