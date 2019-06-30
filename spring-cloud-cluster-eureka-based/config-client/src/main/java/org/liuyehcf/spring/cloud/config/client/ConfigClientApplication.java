package org.liuyehcf.spring.cloud.config.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hechenfeng
 * @date 2018/7/12
 */
@RestController
@EnableEurekaClient
@SpringBootApplication
@RequestMapping("/demo/config")
public class ConfigClientApplication {

    //获取配置中心的属性
    @Value("${host}")
    private String host;

    //获取配置中心的属性
    @Value("${description}")
    private String description;

    public static void main(String[] args) {
        SpringApplication.run(ConfigClientApplication.class, args);
    }

    @GetMapping("/getHost")
    public String getHost() {
        return this.host;
    }

    @GetMapping("/getDescription")
    public String getDescription() {
        return this.description;
    }
}
