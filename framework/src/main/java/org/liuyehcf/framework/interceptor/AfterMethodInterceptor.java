package org.liuyehcf.framework.interceptor;

/**
 * Created by liuye on 2017/5/19 0019.
 */
public class AfterMethodInterceptor implements MethodInterceptor {
    public Object intercept(MethodInvocation invocation) {
        Object res = invocation.process();
        System.out.println("execute BeforeMethodInterceptor#intercept after method");
        return res;
    }
}
