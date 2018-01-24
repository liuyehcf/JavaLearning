package org.liuyehcf.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by Liuye on 2017/12/24.
 */
public class CglibEnhancer implements MethodInterceptor {
    private Enhancer enhancer = new Enhancer();

    public Object getProxy(Class clazz) {
        enhancer.setSuperclass(clazz);

        enhancer.setCallback(this);

        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println(method.getName() + "执行之前做一些准备工作");
        //Object result = method.invoke(obj, args); 想不通
        Object result = methodProxy.invokeSuper(o, objects);
        System.out.println(method.getName() + "执行之后做一些准备的工作");
        return result;
    }
}
