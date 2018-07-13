package org.liuyehcf.spring.cloud.ribbon.consumer;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author hechenfeng
 * @date 2018/7/13
 */
@Service
public class GreetService {
    @Resource
    private RestTemplate restTemplate;

    String sayHi(String name) {
        String reqURL = "http://ZookeeperProvider/hi?name=" + name;
        return restTemplate.getForEntity(reqURL, String.class).getBody();
    }
}
