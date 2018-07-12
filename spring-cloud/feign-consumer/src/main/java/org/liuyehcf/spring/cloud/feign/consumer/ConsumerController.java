package org.liuyehcf.spring.cloud.feign.consumer;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author chenlu
 * @date 2018/7/12
 */
@RestController
@RequestMapping("/demo/feign")
public class ConsumerController {
    @Resource
    private CalculatorService calculatorService;

    @RequestMapping("/sayHi")
    String sayHi(@RequestParam String name) {
        return calculatorService.sayHi(name);
    }
}
