package org.liuyehcf.spring.cloud.feign.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author chenlu
 * @date 2018/7/12
 */
@FeignClient(value = "CalculatorServer")
interface CalculatorService {
    @RequestMapping(value = "/hi", method = RequestMethod.GET)
    String sayHi(@RequestParam("name") String name);
}
