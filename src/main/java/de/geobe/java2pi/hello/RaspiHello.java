package de.geobe.java2pi.hello;

import java.util.Properties;

public class RaspiHello {
    public static void main(String[] args) {
        System.out.printf("Hello from %s on %s\n", System.getProperty("os.name"), System.getProperty("os.arch"));
    }
}
