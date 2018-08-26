package org.liuyehcf.spring.tx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hechenfeng
 * @date 2018/8/11
 */
@SpringBootApplication(scanBasePackages = "org.liuyehcf.spring.tx")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
