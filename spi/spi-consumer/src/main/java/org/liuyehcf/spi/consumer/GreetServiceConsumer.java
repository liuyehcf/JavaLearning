package org.liuyehcf.spi.consumer;

import org.liuyehcf.spi.GreetService;

import java.util.ServiceLoader;

public class GreetServiceConsumer {
    public static void main(String[] args) {
        ServiceLoader<GreetService> load = ServiceLoader.load(GreetService.class);

        for (GreetService serviceFacade : load) {
            System.out.println(serviceFacade.sayHello("liuyehcf"));
        }
    }
}
