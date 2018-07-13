package org.liuyehcf.spring.cloud.zookeeper.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hechenfeng
 * @date 2018/7/13
 */
@SpringBootApplication
public class ZookeeperProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZookeeperProviderApplication.class, args);
    }
}
