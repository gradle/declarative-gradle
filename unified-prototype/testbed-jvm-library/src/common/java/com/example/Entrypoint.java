package com.example;

import java.util.ServiceLoader;

public class Entrypoint {

    public static void main(String[] args) {
        new Entrypoint().run();
    }

    public void run() {
        ServiceLoader.load(Interface.class).forEach(impl -> System.out.println(impl.getValue()));
    }
}
