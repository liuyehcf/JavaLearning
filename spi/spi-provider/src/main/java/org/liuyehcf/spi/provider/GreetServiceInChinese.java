package org.liuyehcf.spi.provider;

import org.liuyehcf.spi.GreetService;

public class GreetServiceInChinese implements GreetService {
    public String sayHello(String name) {
        return "你好，" + name;
    }
}
