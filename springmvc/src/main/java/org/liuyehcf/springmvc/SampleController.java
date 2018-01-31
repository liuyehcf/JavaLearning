package org.liuyehcf.springmvc;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class SampleController {

    @RequestMapping("/home")
    @ResponseBody
    public String hello(String name) {
        return "hello, " + name;
    }
}