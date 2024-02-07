package com.example;

import org.springframework.core.SpringVersion;

public record Seventeen() implements Interface {

    @Override
    public String getValue() {
        return "Java 17. The value is " + new Common().getValue() + ". The spring version is " + SpringVersion.getVersion();
    }

}
