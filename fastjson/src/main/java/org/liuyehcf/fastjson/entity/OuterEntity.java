package org.liuyehcf.fastjson.entity;

/**
 * Created by Liuye on 2017/12/15.
 */
public class OuterEntity<T> {
    private int id;

    private String name;

    private T middleEntity;

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

    public T getMiddleEntity() {
        return middleEntity;
    }

    public void setMiddleEntity(T middleEntity) {
        this.middleEntity = middleEntity;
    }

    public OuterEntity(){}
}
