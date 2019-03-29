package com.example.springaop;

import java.lang.annotation.*;

/**
 * 이 애노테이션은 메소드 실행시간을 측정해 줍니다
 */
// 자바독 생성시 document가 되도록
@Documented
// 애노테이션의 타겟
@Target(ElementType.METHOD)
// Retention : 이 애노테이션 정보를 어디까지 유지할것인가
@Retention(RetentionPolicy.CLASS)
public @interface PerfLogging {
}
