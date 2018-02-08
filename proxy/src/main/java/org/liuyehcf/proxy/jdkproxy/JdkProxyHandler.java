package org.liuyehcf.proxy.jdkproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by Liuye on 2017/12/24.
 */
public class JdkProxyHandler implements InvocationHandler {
    private Object obj;

    public JdkProxyHandler(Object obj) {
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("pre-processor");

        Object result = method.invoke(obj, args);

        System.out.println("after-processor");

        return result;
    }
}
