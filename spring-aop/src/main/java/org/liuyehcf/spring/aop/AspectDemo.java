package org.liuyehcf.spring.aop;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AspectDemo {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        HelloService helloService = context.getBean(HelloService.class);
        SimpleSpringAdvisor simpleSpringAdvisor = context.getBean(SimpleSpringAdvisor.class);

        System.out.println(simpleSpringAdvisor.getClass().getName());

        System.out.println(helloService.getClass().getName());

        context.getBean(HelloService.class).sayHello();
    }
}
