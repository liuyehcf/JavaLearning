package org.liuyehcf.springboot.dataobject;

/**
 * Created by Liuye on 2017/12/15.
 */
public class LoginRequest {
    private String name;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
