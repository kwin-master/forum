package com.kwin.forum.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class TestAspect {
    @Pointcut("execution(* com.kwin.forum.service.*.*(..))")
    public void pointcut() {}
//
//    @Before("pointcut()")
//    public void before() {
//        System.out.println("前置通知");
//    }
//
//    @AfterReturning("pointcut()")
//    public void afterReturning() {
//        System.out.println("后置通知");
//    }
//
//    @AfterThrowing("pointcut()")
//    public void afterThrowing() {
//        System.out.println("异常通知");
//    }
//
//    @After("pointcut()")
//    public void after() {
//        System.out.println("最终通知");
//    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint pjp) {
        Object rtValue = null;
        try {
            System.out.println("前置通知");
            Object[] args = pjp.getArgs();
            rtValue = pjp.proceed(args);
            System.out.println("后置通知");
            return rtValue;
        }catch (Throwable t) {
            System.out.println("异常通知");
            throw new RuntimeException(t);
        }finally {
            System.out.println("最终通知");
        }
    }
}
