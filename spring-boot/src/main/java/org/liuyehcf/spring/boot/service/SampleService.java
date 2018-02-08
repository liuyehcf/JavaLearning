package org.liuyehcf.spring.boot.service;

import org.liuyehcf.spring.boot.dataobject.LoginRequest;
import org.liuyehcf.spring.boot.dataobject.LoginResponse;
import org.springframework.stereotype.Service;

/**
 * Created by HCF on 2017/11/24.
 */
@Service
public class SampleService {
    public String home() {
        return "Hello world!";
    }

    public LoginResponse login(LoginRequest loginRequest) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setState("OK");
        loginResponse.setMessage("欢迎登陆" + loginRequest.getName());
        return loginResponse;
    }

    public String compute(String value1, String value2, String operator) {
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
}
