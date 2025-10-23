package com.linkme.backend.common;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(public * com.linkme.backend.module..*(..))")
    public void modules() {}

    @Before("modules()")
    public void before(JoinPoint jp) {
        System.out.println("[LOG] -> " + jp.getSignature());
    }

    @AfterReturning(pointcut="modules()", returning="ret")
    public void after(JoinPoint jp, Object ret) {
        System.out.println("[LOG] <- " + jp.getSignature());
    }
}
