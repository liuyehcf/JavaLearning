package org.liuyehcf.controller;

import org.liuyehcf.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by HCF on 2017/11/24.
 */
@Controller
@RequestMapping("/home")
public class SampleController {

    @Autowired
    private SampleService sampleService;

    @RequestMapping("/hello")
    @ResponseBody
    public String hello(){
        return sampleService.getHelloWorld();
    }

}
