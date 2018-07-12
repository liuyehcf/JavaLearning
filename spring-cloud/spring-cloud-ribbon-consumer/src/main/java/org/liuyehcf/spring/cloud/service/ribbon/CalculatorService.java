package org.liuyehcf.spring.cloud.service.ribbon;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author chenlu
 * @date 2018/7/12
 */
@Service
class CalculatorService {
    @Resource
    private RestTemplate restTemplate;

    String sayHi(String name) {
        String reqURL = "http://CalculatorServer/hi?name=" + name;
        return restTemplate.getForEntity(reqURL, String.class).getBody();
    }
}
