package com.example;

import com.google.common.collect.ImmutableList;

public class App {
    public static void main(String[] args) {
        // Verify that Guava is available
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        builder.add("Hello from Java " + System.getProperty("java.version"));

        ImmutableList<String> messages = builder.build();
        messages.forEach(System.out::println);
    }
}