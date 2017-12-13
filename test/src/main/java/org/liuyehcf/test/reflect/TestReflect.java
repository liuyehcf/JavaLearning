package org.liuyehcf.test.reflect;

import org.junit.Test;

import java.lang.reflect.Method;

/**
 * Created by liuye on 2017/4/15 0015.
 */
public class TestReflect {

    final static class A{
        public void sayHello(){
            System.out.println("hello world!");
        }
    }

    /**
     * 测试给定Object对象，在不知道其真实类型的情况下，是否可以调用方法
     */
    @Test
    public void test1() throws Exception{
        Object obj=new A();
        Class<?> clzz=obj.getClass();
        Method[] methods=clzz.getMethods();
        final String  methodName="sayHello";
        for(Method method:methods){
            if(method.getName().equals(methodName)){
                method.invoke(obj);
            }
        }
    }
}
