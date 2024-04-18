package com.example;

public class Eleven implements Interface {

    @Override
    public String getValue() {
        var result = "Java 11. The value is " + new Common().getValue() + ". Hibernate version is " + org.hibernate.Version.getVersionString();
        return result;
    }

}
