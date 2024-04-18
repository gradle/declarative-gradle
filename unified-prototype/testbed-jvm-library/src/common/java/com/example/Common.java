package com.example;

import org.apache.commons.lang3.StringUtils;

public class Common {
    public String getValue() {
        String value = StringUtils.valueOf(new char[] { '4', '2' });
        String version = "The runtime java version is " + System.getProperty("java.version");
        return value + ". " + version;
    }
}
