package org.example.utilities

import org.example.list.LinkedList

object SplitUtils {
    fun split(source: String): LinkedList {
        var lastFind = 0
        var currentFind: Int
        val result: LinkedList = LinkedList()

        while ((source.indexOf(" ", lastFind).also { currentFind = it }) != -1) {
            var token: String = source.substring(lastFind)
            if (currentFind != -1) {
                token = token.substring(0, currentFind - lastFind)
            }

            addIfValid(token, result)
            lastFind = currentFind + 1
        }

        val token: String = source.substring(lastFind)
        addIfValid(token, result)

        return result
    }

    private fun addIfValid(token: String, list: LinkedList) {
        if (isTokenValid(token)) {
            list.add(token)
        }
    }

    private fun isTokenValid(token: String): Boolean {
        return !token.isEmpty()
    }
}
