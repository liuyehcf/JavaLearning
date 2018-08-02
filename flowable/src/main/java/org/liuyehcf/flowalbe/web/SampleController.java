package org.liuyehcf.flowalbe.web;

import org.liuyehcf.flowalbe.service.SampleProcessService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author chenlu
 * @date 2018/7/25
 */
@RestController
public class SampleController {

    @Resource
    private SampleProcessService sampleProcessService;

    @RequestMapping("/deploy")
    @ResponseBody
    public String deploy() {
        sampleProcessService.deploy();
        return "Deploy Succeeded";
    }

    @RequestMapping("/start")
    @ResponseBody
    public String start() {
        sampleProcessService.start();
        return "Start Succeeded";
    }

    @RequestMapping("/login")
    @ResponseBody
    public String login(@RequestParam("name") String name, @RequestParam("password") String password) {
        sampleProcessService.login(name, password);
        return "Login Succeeded";
    }

    @RequestMapping("/shopping")
    @ResponseBody
    public String shopping() {
        sampleProcessService.shopping();
        return "Shopping Succeeded";
    }
}
