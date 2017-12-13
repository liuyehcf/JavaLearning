package org.liuyehcf.jdkproxy;

import sun.misc.ProxyGenerator;

import java.io.FileOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by HCF on 2017/7/28.
 */
interface Person {
    void sayHello();
}

public class TestJdkProxy implements Person {
    public void sayHello() {
        System.out.println("TestJdkProxy says hello");
    }


    private static final class MyInvocationHandler implements InvocationHandler {
        public MyInvocationHandler(Object obj) {
            this.obj = obj;
        }

        private Object obj;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("pre-processor");

            Object result = method.invoke(obj, args);

            System.out.println("after-processor");

            return result;
        }
    }

    public static void main(String[] args) throws Exception {


        TestJdkProxy target = new TestJdkProxy();

        MyInvocationHandler invocationHandler = new MyInvocationHandler(target);

        Person p = (Person) Proxy.newProxyInstance(target.getClass().getClassLoader(), new Class<?>[]{Person.class}, invocationHandler);

        p.sayHello();

        byte[] classFile = ProxyGenerator.generateProxyClass("MyProxy", TestJdkProxy.class.getInterfaces());

        FileOutputStream out;

        try{
            out = new FileOutputStream("/Users/HCF/Desktop/MyProxy.class");
            out.write(classFile);
            out.flush();
        }catch(Exception e){

        }
    }
}
