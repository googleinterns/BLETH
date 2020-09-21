package com.google.research.bleth.simulator;

public class HelloWorld {
    /** Greeting */
    public static String message() {
        Person omer;
        omer = Person.newBuilder()
                .setId(1)
                .setName("Omer Madmon")
                .setEmail("omermadmon@gmail.com")
                .build();
        return  "Hello World!";
    }
}
