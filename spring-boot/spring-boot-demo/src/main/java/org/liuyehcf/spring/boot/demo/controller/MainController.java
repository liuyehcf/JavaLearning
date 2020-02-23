package org.liuyehcf.spring.boot.demo.controller;

import org.liuyehcf.spring.boot.demo.dto.LoginRequestDTO;
import org.liuyehcf.spring.boot.demo.dto.LoginResponseDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by HCF on 2017/11/24.
 */
@Controller
@RequestMapping("/")
public class MainController {

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    @ResponseBody
    public String home() {
        return "Hello world!";
    }

    @RequestMapping(value = "/echo", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String echo(@RequestParam String content) {
        return content;
    }

    @RequestMapping(value = "/echoBody", method = {RequestMethod.POST})
    @ResponseBody
    public String echo(@RequestParam String queryContent, @RequestBody String bodyContent) {
        return queryContent + ":" + bodyContent;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setState("OK");
        loginResponse.setMessage("欢迎登陆" + request.getName());
        return loginResponse;
    }

    @RequestMapping(value = "/compute", method = RequestMethod.GET)
    @ResponseBody
    public String compute(@RequestParam String value1,
                          @RequestParam String value2,
                          @RequestHeader String operator) {
        switch (operator) {
            case "+":
                return Float.toString(
                        Float.parseFloat(value1)
                                + Float.parseFloat(value2));
            case "-":
                return Float.toString(
                        Float.parseFloat(value1)
                                - Float.parseFloat(value2));
            case "*":
                return Float.toString(
                        Float.parseFloat(value1)
                                * Float.parseFloat(value2));
            default:
                return "wrong operation";
        }
    }

    @RequestMapping(value = "/user/get", method = RequestMethod.GET)
    @ResponseBody
    public String getUser(@RequestParam int id) {
        return String.format("{\n" +
                "    \"id\":%d,\n" +
                "    \"firstName\":\"三\",\n" +
                "    \"lastName\":\"张\",\n" +
                "    \"age\":20\n" +
                "}", id);
    }
}