package org.liuyehcf.lambda;

import java.util.List;

public class LambdaDemo {
    public static void main(String[] args) {
        int i=5;
        DemoFunctionalInterface demoFunctionalInterface = (objects) -> {
            return null;
        };

        System.out.println(demoFunctionalInterface.doSomething(null));

        List list=null;
        list.stream();
    }
}
