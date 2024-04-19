package com.example;

import org.springframework.core.SpringVersion;

public class Seventeen implements Interface {
    @Override
    public String getValue() {
        return "Java 17. The spring version is " + SpringVersion.getVersion();
    }
}
