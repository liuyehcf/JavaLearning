package org.liuyehcf.fastjson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Liuye on 2017/12/15.
 */
public class GenericTypeDemo<T> {

    private static class MyTypeReference<T> {
        private final Type type;

        private MyTypeReference() {
            Type superClass = getClass().getGenericSuperclass();

            Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];

            this.type = type;
        }

        public Type getType() {
            return type;
        }
    }

    public static void main(String[] args) {
        System.out.println(
                new MyTypeReference<List<String>>() {
                    // override nothing
                }.getType().getTypeName());
    }
}
