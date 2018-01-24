package org.liuyehcf.interceptor.test;

import org.junit.Test;
import org.liuyehcf.interceptor.AfterMethodInterceptor;
import org.liuyehcf.interceptor.BeforeMethodInterceptor;
import org.liuyehcf.interceptor.base.DefaultMethodInvocation;

/**
 * Created by liuye on 2017/5/19 0019.
 */
public class TestInterceptor {

    @Test
    public void testInterceptor() {
        DefaultMethodInvocation invocation = new DefaultMethodInvocation(new Person(), "sayHello", new Object[0]);

        invocation.add(new BeforeMethodInterceptor());

        invocation.add(new AfterMethodInterceptor());

        invocation.process();
    }
}
