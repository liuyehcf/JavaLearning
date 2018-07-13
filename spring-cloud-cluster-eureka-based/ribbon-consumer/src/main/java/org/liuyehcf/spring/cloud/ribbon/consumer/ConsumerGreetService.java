package org.liuyehcf.spring.cloud.ribbon.consumer;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author hechenfeng
 * @date 2018/7/12
 */
@Service
class ConsumerGreetService {
    @Resource
    private RestTemplate restTemplate;

    String sayHi(String name) {
        String reqURL = "http://GreetService/hi?name=" + name;
        return restTemplate.getForEntity(reqURL, String.class).getBody();
    }
}
