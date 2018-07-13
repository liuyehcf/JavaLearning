package org.liuyehcf.spring.cloud.feign.consumer;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author hechenfeng
 * @date 2018/7/13
 */
@RestController
@RequestMapping("/demo/feign")
public class ConsumerGreetController {
    @Resource
    private ConsumerGreetService consumerGreetService;

    @RequestMapping("/sayHi")
    String sayHi(@RequestParam String name) {
        return consumerGreetService.sayHi(name);
    }
}
