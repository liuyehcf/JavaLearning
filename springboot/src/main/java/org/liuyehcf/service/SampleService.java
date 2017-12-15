package org.liuyehcf.service;

import org.liuyehcf.entity.LoginRequest;
import org.liuyehcf.entity.LoginResponse;
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

    public String add(String value1, String value2) {
        return Float.toString(
                Float.parseFloat(value1)
                        + Float.parseFloat(value2));
    }
}
