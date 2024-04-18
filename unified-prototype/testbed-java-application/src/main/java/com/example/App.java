package com.example;

import com.example.utils.Utils;
import com.google.common.collect.ImmutableList;

public class App {
    public static void main(String[] args) {
        // Verify that Guava is available
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        builder.add("Hello from Java " + System.getProperty("java.version"));

        // Verify that the Java library is available
        Utils utils = new Utils();
        builder.add(utils.getWelcome());

        ImmutableList<String> messages = builder.build();
        messages.forEach(System.out::println);
    }
}