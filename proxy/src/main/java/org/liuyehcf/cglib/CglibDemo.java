package org.liuyehcf.cglib;

/**
 * Created by Liuye on 2017/12/24.
 */
public class CglibDemo {
    public static void main(String[] args) {
        CglibEnhancer enhancer = new CglibEnhancer();
        CglibClient proxy = (CglibClient) enhancer.getProxy(CglibClient.class);
        proxy.sayHello();
    }
}
