package org.liuyehcf.spring.boot.demo.dto;

/**
 * Created by Liuye on 2017/12/15.
 */
public class LoginResponseDTO {
    private String state;

    private String message;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
