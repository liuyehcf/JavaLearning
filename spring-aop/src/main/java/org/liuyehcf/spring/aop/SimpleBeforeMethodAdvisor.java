package org.liuyehcf.spring.aop;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * Created by HCF on 2018/1/4.
 */
public class SimpleBeforeMethodAdvisor implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] objects, Object o) throws Throwable {
        System.out.println("before method advice");
    }
}
