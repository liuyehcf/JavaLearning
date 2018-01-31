package org.liuyehcf.springboot.controller;

import org.liuyehcf.springboot.dataobject.LoginRequest;
import org.liuyehcf.springboot.dataobject.LoginResponse;
import org.liuyehcf.springboot.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by HCF on 2017/11/24.
 */
@Controller
@RequestMapping("/")
public class SampleController {

    @Autowired
    private SampleService sampleService;

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    @ResponseBody
    public String home() {
        return sampleService.home();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public LoginResponse login(@RequestBody LoginRequest request) {
        return sampleService.login(request);
    }

    @RequestMapping(value = "/compute", method = RequestMethod.GET)
    @ResponseBody
    public String compute(@RequestParam String value1, @RequestParam String value2, @RequestHeader String operator) {
        return sampleService.compute(value1, value2, operator);
    }

}
