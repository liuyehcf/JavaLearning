package org.liuyehcf.fastjson.entity;

/**
 * Created by Liuye on 2017/12/15.
 */
public class MiddleEntity<T> {
    private int id;

    private String name;

    private T innerEntity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getInnerEntity() {
        return innerEntity;
    }

    public void setInnerEntity(T innerEntity) {
        this.innerEntity = innerEntity;
    }

    public MiddleEntity() {
    }
}
