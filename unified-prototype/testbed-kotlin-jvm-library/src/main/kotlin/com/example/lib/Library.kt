package com.example.lib

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap

class Library {
    // Verify that Guava is available
    val values: ListMultimap<String, Long> = ArrayListMultimap.create()
}