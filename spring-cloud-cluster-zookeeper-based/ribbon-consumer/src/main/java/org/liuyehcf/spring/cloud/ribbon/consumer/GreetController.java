package org.liuyehcf.spring.cloud.ribbon.consumer;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author hechenfeng
 * @date 2018/7/13
 */
@RestController
@RequestMapping("/demo/ribbon")
public class GreetController {
    @Resource
    private GreetService greetService;

    @RequestMapping("/sayHi")
    String sayHi(@RequestParam String name) {
        return greetService.sayHi(name);
    }

}
