package org.liuyehcf.spring.tx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author hechenfeng
 * @date 2018/8/11
 */
@SpringBootApplication
@ComponentScan("org.liuyehcf.spring.tx")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
