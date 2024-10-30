package org.example.list

class LinkedList {
    private var head: Node? = null

    fun add(element: String?) {
        val newNode = Node(element)

        val it = tail(head)
        if (it == null) {
            head = newNode
        } else {
            it.next = newNode
        }
    }

    fun remove(element: String): Boolean {
        var result = false
        var previousIt: Node? = null
        var it: Node?
        it = head
        while (!result && it != null) {
            if (0 == element.compareTo(it.data!!)) {
                result = true
                unlink(previousIt, it)
                break
            }
            previousIt = it
            it = it.next
        }

        return result
    }

    private fun unlink(previousIt: Node?, currentIt: Node) {
        if (currentIt === head) {
            head = currentIt.next
        } else {
            previousIt!!.next = currentIt.next
        }
    }

    fun size(): Int {
        var size = 0

        var it = head
        while (it != null) {
            ++size
            it = it.next
        }

        return size
    }

    fun get(index: Int): String? {
        var currIdx = index
        var it = head
        while (currIdx > 0 && it != null) {
            it = it.next
            currIdx--
        }

        if (it == null) {
            throw java.lang.IndexOutOfBoundsException("Index is out of range")
        }

        return it.data
    }

    private class Node(val data: String?) {
        var next: Node? = null
    }

    companion object {
        private fun tail(head: Node?): Node? {
            var it: Node?

            it = head
            while (it?.next != null) {
                it = it.next
            }

            return it
        }
    }
}
