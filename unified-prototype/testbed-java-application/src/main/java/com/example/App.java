package com.example;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

public class App {
    private final ListMultimap<String, Long> values = ImmutableListMultimap.of();

    public static void main(String[] args) {
        System.out.println("Hello from Java " + System.getProperty("java.version"));
    }
}