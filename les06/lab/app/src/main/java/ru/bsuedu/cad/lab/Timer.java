package ru.bsuedu.cad.lab;

import org.springframework.stereotype.Component;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Component
@Aspect
public class Timer {
    @Around("execution(* ru.bsuedu.cad.lab.CSVParser.parse(..))")
    public Object measureParsingTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println("Parsing time: " + elapsedTime + " ms");
        return result;
    }
}
