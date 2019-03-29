# Spring-AOP

## Spring AOP 특징
- 프록시 기반의 AOP 구현체
- 스프링 빈에만 AOP를 적용할 수 있다.

###  사용 예제
1. interface 생성(EventService)
2. 해당 interface를 구현한 Service 객체 생성(SimpleEventService)
3. 만든 Service에서 메소드 정의
4. 해당 메소드에서 공통된 코드를 사용할 경우 interface를 구현하는 ProxyService 생성(ProxySimpleEventService)
5. 원래 Service는 수정하지 않고 새로 생성한 ProxyService에서 공통 코드를 추가시켜 사용

    - 해당 ProxyService를 사용하면 원본 Service는 수정하지 않고 ProxyService 에서만 수정해서 사용할 수 있지만 그래도 공통코드를 직접 다 적어줘야 하는 불편함이 있다.
    - Spring Annotation을 사용하면 이런 번거로움을 줄일수 있다.
        
### Annotation
1. pom.xml에 의존성을 추가한다.
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

2. 애스팩트 정의
    1. Aspect 클래스를 생성(PerfAspect)
    2. 해당 클래스를 @Aspect로 지정한다.
    3. 해당 클래스에서 공통으로 실행될 코드를 작성하고 @Around 를 사용하여 실행될 위치, 경로(Around)를 설정할 수 있다.
        - @Around("execution(* com.example..*.EventService.*(..))") : execution으로 실행될 메소드를 지정할 수 있다.
        - @Around("bean(simpleEventService)") : 해당 빈의 메소드에만 적용할 수 있다.
        - @Around("@annotation(PerfLogging)") : 해당 annotation을 설정한 메소드에만 실행할 수 있다.
            - annotation 생성 방법
                1. annotation을 생성한다. (PeftLogging)
                2. 애노테이션의 설정을 한다.
                    - @Retention(RetentionPolicy.CLASS) : 이 애노테이션 정보를 어디까지 유지할것인가
                    - @Target(ElementType.METHOD) : 이 애노테이션의 타겟
                    - @Documented : 자바독 생성시 독타입 설정
                3. 해당 애노테이션이 실행 될 메소드에 애노테이션을 입력한다 (SimpleEventService)           
    4. @Before 애노테이션을 사용하여 메소드가 실행되기전에 실행되는 코드를 작성 할 수도 있다.(PerfAspect)
    