package org.liuyehcf.reflect.dto;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class UserDTO<Data> {
    private String name;
    private Integer age;
    private Map<String, Data> map;
    private List<Data> list;
    private Set<Data> set;
    private Queue<Data> queue;

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

    public List<Data> getList() {
        return list;
    }

    public void setList(List<Data> list) {
        this.list = list;
    }

    public Set<Data> getSet() {
        return set;
    }

    public void setSet(Set<Data> set) {
        this.set = set;
    }

    public Queue<Data> getQueue() {
        return queue;
    }

    public void setQueue(Queue<Data> queue) {
        this.queue = queue;
    }
}
