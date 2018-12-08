package org.liuyehcf.spi.provider;

import org.liuyehcf.spi.GreetService;

public class GreetServiceInEnglish implements GreetService {
    public String sayHello(String name) {
        return "hello, " + name;
    }
}
