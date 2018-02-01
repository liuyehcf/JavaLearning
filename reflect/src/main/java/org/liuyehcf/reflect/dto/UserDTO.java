package org.liuyehcf.reflect.dto;

import java.util.List;
import java.util.Map;

public class UserDTO<Data> {
    private String name;
    private Integer age;
    private Map<String, Data> map;
    private List<Data> params;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Map<String, Data> getMap() {
        return map;
    }

    public void setMap(Map<String, Data> map) {
        this.map = map;
    }

    public List<Data> getParams() {
        return params;
    }

    public void setParams(List<Data> params) {
        this.params = params;
    }
}
