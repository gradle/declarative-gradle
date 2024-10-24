package com.example;

public class TwentyOne implements Interface {

    @Override
    public String getValue() {
        var result = "Java 21. The value is " + new Common().getValue() + ". Hibernate version is " + org.hibernate.Version.getVersionString();
        return result;
    }

}
