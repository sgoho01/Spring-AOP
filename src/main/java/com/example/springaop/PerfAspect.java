package com.example.springaop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class PerfAspect {

    // com.example 패키지 아래에 있는 모든 클래스중 EventService안에 들어있는 모든 메소드에 이 행위를 적용해라
    //@Around("execution(* com.example..*.EventService.*(..))")
    // 해당 애노테이션(PerfLogging)이 설정되어 있는 메소드에만 적용
    //@Around("@annotation(PerfLogging)")
    // 해당 빈에만 적용
    @Around("bean(simpleEventService)")
    public Object logPefrf(ProceedingJoinPoint pjp) throws Throwable {
        long begin = System.currentTimeMillis();
        // 원래 메소드 실행
        Object retVal = pjp.proceed();
        System.out.println(System.currentTimeMillis() - begin);
        return retVal;

    }

    // Before 애노테이션은 메소드 실행전에 실행하라
    @Before("bean(simpleEventService)")
    public void hello() {
        System.out.println("hello");
    }

}
