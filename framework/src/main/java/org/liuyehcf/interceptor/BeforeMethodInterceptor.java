package org.liuyehcf.interceptor;

/**
 * Created by liuye on 2017/5/19 0019.
 */
public class BeforeMethodInterceptor implements MethodInterceptor {
    public Object intercept(MethodInvocation invocation) {
        System.out.println("execute BeforeMethodInterceptor#intercept before method");
        return invocation.process();
    }
}
