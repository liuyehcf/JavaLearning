package org.liuyehcf.fastjson;

import java.util.List;

/**
 * Created by Liuye on 2017/12/15.
 */
public class GenericTypeDemo<T> {

    public static void main(String[] args) {
        System.out.println(
                new MyTypeReference<List<List<String>>>() {
                    // override nothing
                }.getType().getTypeName());
    }
}
