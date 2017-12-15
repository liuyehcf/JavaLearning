package org.liuyehcf.fastjson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by HCF on 2017/12/15.
 */
public abstract class MyTypeReference<T> {
    private final Type type;

    public MyTypeReference() {
        Type superClass = getClass().getGenericSuperclass();

        Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];

        this.type = type;
    }

    public final Type getType() {
        return type;
    }
}