package org.liuyehcf.spring.cloud.ribbon.consumer;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author hechenfeng
 * @date 2018/7/12
 */
@RestController
@RequestMapping("/demo/ribbon")
public class ConsumerController {
    @Resource
    private CalculatorService calculatorService;

    @RequestMapping("/sayHi")
    String sayHi(@RequestParam String name) {
        return calculatorService.sayHi(name);
    }
}
