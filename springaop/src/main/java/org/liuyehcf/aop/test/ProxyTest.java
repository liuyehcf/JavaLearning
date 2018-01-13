package org.liuyehcf.aop.test;

import org.junit.Test;
import org.liuyehcf.aop.ForumService;
import org.liuyehcf.aop.ForumServiceImpl;
import org.liuyehcf.aop.cglib.CglibProxy;
import org.liuyehcf.aop.jdkdp.PerformanceHandler;

import java.lang.reflect.Proxy;

/**
 * Created by liuye on 2017/5/17 0017.
 */
public class ProxyTest {

    @Test
    public void testJdkDp(){
        ForumService target=new ForumServiceImpl();

        PerformanceHandler handler=new PerformanceHandler(target);

        ForumService proxy=(ForumService) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                handler
        );
        proxy.removeForum(10);
        proxy.removeTopic(20);
    }

    @Test
    public void testCGLib(){
        CglibProxy proxy=new CglibProxy();
        ForumServiceImpl forumService = (ForumServiceImpl) proxy.getProxy(ForumServiceImpl.class);

        forumService.removeForum(10);
        forumService.removeTopic(1023);
    }
}


