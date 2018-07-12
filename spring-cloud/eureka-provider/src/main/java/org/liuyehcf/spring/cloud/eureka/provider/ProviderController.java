package org.liuyehcf.spring.cloud.eureka.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author hechenfeng
 * @date 2018/7/12
 */
@RestController
public class ProviderController {
    @Value("${server.port}")
    private String port;

    @RequestMapping("/hi")
    public String hi(@RequestParam String name) {
        return "hi " + name + ", I'm from port " + port + ", current Time is " + new Date();
    }
}
