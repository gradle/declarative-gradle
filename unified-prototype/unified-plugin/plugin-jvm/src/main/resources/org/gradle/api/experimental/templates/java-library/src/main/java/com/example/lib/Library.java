package com.example.lib;

import com.google.common.collect.ImmutableList;

public class Library {
    public Iterable<String> getMessages() {
        // Verify that Guava is available
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        builder.add("Hello from Java " + System.getProperty("java.version"));

        return builder.build();
    }
}
