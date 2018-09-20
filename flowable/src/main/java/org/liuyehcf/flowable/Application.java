package org.liuyehcf.flowable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hechenfeng
 * @date 2018/7/25
 */
@SpringBootApplication(scanBasePackages = "org.liuyehcf.flowable")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
