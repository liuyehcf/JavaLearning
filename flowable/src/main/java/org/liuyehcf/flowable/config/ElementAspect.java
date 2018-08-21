package org.liuyehcf.flowable.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author hechenfeng
 * @date 2018/8/17
 */
@Aspect
@Component
public class ElementAspect {
    @Around("execution(* org.liuyehcf.flowable.element.*.*(..))")
    public Object taskAround(ProceedingJoinPoint proceedingJoinPoint) {

        Object[] args = proceedingJoinPoint.getArgs();

        try {
            return proceedingJoinPoint.proceed(args);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
