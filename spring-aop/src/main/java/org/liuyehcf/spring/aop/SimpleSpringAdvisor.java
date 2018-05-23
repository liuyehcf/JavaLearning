package org.liuyehcf.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
public class SimpleSpringAdvisor {
    @Pointcut("execution(* org.liuyehcf.spring.aop.HelloService.*(..))")
    public void pointCut() {
    }

    @After("pointCut()")
    public void after(JoinPoint joinPoint) {
        System.out.println("after aspect executed");
    }

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {

        System.out.println("before aspect executing");
    }

    @AfterReturning(pointcut = "pointCut()", returning = "returnVal")
    public void afterReturning(JoinPoint joinPoint, Object returnVal) {
        System.out.println("afterReturning executed, return result is "
                + returnVal);
    }

    @Around("pointCut()")
    public void around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("around start..");
        Class<?> clazz = null;
        String methodName = null;
        Object[] args = null;
        Object result = null;
        try {
            clazz = proceedingJoinPoint.getSignature().getDeclaringType();
            methodName = proceedingJoinPoint.getSignature().getName();
            args = proceedingJoinPoint.getArgs();
            result = proceedingJoinPoint.proceed(args);
        } catch (Throwable ex) {
            System.out.println("error in around");
            throw ex;
        } finally {
            System.out.println("class: " + clazz);
            System.out.println("methodName: " + methodName);
            System.out.println("args: " + Arrays.toString(args));
            System.out.println("result: " + result);
        }
    }

    @AfterThrowing(pointcut = "pointCut()", throwing = "error")
    public void afterThrowing(JoinPoint jp, Throwable error) {
        System.out.println("error:" + error);
    }
}
