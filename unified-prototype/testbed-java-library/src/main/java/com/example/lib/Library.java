package com.example.lib;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

public class Library {
    private final ListMultimap<String, Long> values = ImmutableListMultimap.of();
}
