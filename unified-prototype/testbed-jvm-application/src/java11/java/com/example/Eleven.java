package com.example;

public class Eleven implements Interface {
    @Override
    public String getValue() {
        return "Java 11. Hibernate version is " + org.hibernate.Version.getVersionString();
    }
}
