package com.example;

import com.example.utils.Utils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class App {
    public static void main(String[] args) {
        // Verify that Guava is available
        ListMultimap<String, Long> values = ArrayListMultimap.create();
        values.put("Hello from Java " + System.getProperty("java.version"), 12L);

        // Verify that the Java library is available
        values.put(new Utils().getWelcome(), 11L);

        values.keySet().forEach(System.out::println);
    }
}