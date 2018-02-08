package org.liuyehcf.spring.aop;

import org.springframework.stereotype.Service;

@Service
public class HelloService {
    public void sayHello() {
        System.out.println("hello");
    }
}
