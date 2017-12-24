package org.liuyehcf.jdkproxy;

import sun.misc.ProxyGenerator;

import java.io.FileOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by HCF on 2017/7/28.
 */


public class JdkProxyDemo {

    public static void main(String[] args) throws Exception {


        Chinese target = new Chinese();

        JdkProxyHandler jdkProxyHandler = new JdkProxyHandler(target);

        Person p = (Person) Proxy.newProxyInstance(target.getClass().getClassLoader(), new Class<?>[]{Person.class}, jdkProxyHandler);

        p.sayHello();

        byte[] classFile = ProxyGenerator.generateProxyClass("MyProxy", JdkProxyDemo.class.getInterfaces());

        FileOutputStream out;

        try {
            out = new FileOutputStream("proxy/target/MyProxy.class");
            out.write(classFile);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
