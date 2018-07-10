package org.liuyehcf.spring.validate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author chenlu
 * @date 2018/7/10
 */
@EnableAutoConfiguration
@ComponentScan("org.liuyehcf.spring.validate")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
