package org.liuyehcf.spring.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;


public class SimpleMethodInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        System.out.println("before invoke");
        Object result = methodInvocation.proceed();
        System.out.println("after invoke");
        return result;
    }
}
