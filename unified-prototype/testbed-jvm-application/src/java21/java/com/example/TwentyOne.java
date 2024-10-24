package com.example;

public class TwentyOne implements Interface {
    @Override
    public String getValue() {
        return "Java 21. Hibernate version is " + org.hibernate.Version.getVersionString();
    }
}
