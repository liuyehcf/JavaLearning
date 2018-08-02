package org.liuyehcf.flowalbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author chenlu
 * @date 2018/7/25
 */
@SpringBootApplication
@ComponentScan(basePackages = "org.liuyehcf.flowalbe")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
