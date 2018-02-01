package org.liuyehcf.reflect.dto;

public class GenericDTO<Data, Value> {
    private Data data;
    private Value value;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }
}
