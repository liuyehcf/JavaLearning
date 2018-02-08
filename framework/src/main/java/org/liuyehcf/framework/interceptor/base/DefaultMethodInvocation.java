package org.liuyehcf.framework.interceptor.base;

import org.liuyehcf.framework.interceptor.MethodInterceptor;
import org.liuyehcf.framework.interceptor.MethodInvocation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuye on 2017/5/19 0019.
 */
public class DefaultMethodInvocation implements MethodInvocation {
    private Class<?> targetClass;

    private Object target;

    private String methodName;

    private Method method;

    private Object[] args;

    private List<MethodInterceptor> interceptors;

    private int index;

    public DefaultMethodInvocation(Object target, String methodName, Object[] args) {
        this.target = target;
        this.methodName = methodName;
        this.method = null;
        this.args = args;
        this.targetClass = target.getClass();
        this.interceptors = new ArrayList<MethodInterceptor>();
        this.index = -1;
    }

    public Object process() {
        if (++index == interceptors.size()) {
            Object res = null;
            try {
                Method method = getMethod();
                res = method.invoke(target, args);
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
            return res;
        } else {
            MethodInterceptor interceptor = interceptors.get(index);
            return interceptor.intercept(this);
        }
    }

    public void add(MethodInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    private Method getMethod() {
        if (method != null) {
            return method;
        }
        Class<?>[] paramTypes = new Class[this.args.length];
        for (int i = 0; i < this.args.length; i++) {
            paramTypes[i] = this.args[0].getClass();
        }
        Method method = null;
        try {
            method = this.targetClass.getMethod(this.methodName, paramTypes);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return method;
    }
}
