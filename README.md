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
    
    
    
    
# 핸들러 메소드 (Handler Method)

## 핸들러 메소드

---
### 목차
1. [URI 패턴](#1-URI)
2. [요청 매개변수](#2)
3. [@ModelAttribute](#3-ModelAttribute)
4. [Validated](#4-Validated)
5. [폼 서브밋(에러처리)](#5)
6. [@SessionAttributes](#6-SessionAttributes)
7. [멀티 폼 서브밋](#7)
8. [@SessionAttribute](#8-SesstionAttribute)
9. [@RedirectAttributes](#9-RedirectAttributes)
10. [Flash Attributes](#10-Flash-Attributes)
11. [MultipartFile](#11-MultipartFile)
12. [@ResponseEntity](#12-ResponseEntity)
13. [@RequestBody & HttpEntity](#13-RequestBody-HttpEntity)
14. [@ResponseBody & ResponseEntity](#14-ResponseBody-ResponseEntity)
15. [@ModelAttribute](#15-ModelAttribute)
16. [@InitBinder](#16-InitBinder)
---


#
## 1. URI 패턴
- @PathVariable
  - 요청 URI 패턴의 일부를 핸들러 메소드 아규먼트로 받는 방법
  - 타입 변환 지원
  - (기본)값이 반드시 있어야 함
  - Optional 지원
```java
    // Controller.java
    @GetMapping("/event/{id}")
    @ResponseBody
    public Event getEvnet(@PathVariable(required = false) int id) {
        Event event = new Event();
        event.setId(id);
        return event;
    }
    
    
    // Test.java
    @Test
    public void getEvent() throws Exception {
        mockMvc.perform(get("/event/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                ;
    }
    
```

- @MatrixVariable
  - 요청 URI 패턴에서 키/값 쌍의 데이터를 메소드 아규먼트로 받는 방법
  - 타입 변환 지원
  - (기본)값이 반드시 있어야 한다.
  - Optional 지원
  - 이 기능은 기본적으로 비활성화 되어 있음. 활성화 하려면 다음과 같이 설정해야 함.
```java
    // WebConfig.java
    @Configurable
    public class WebConfig implements WebMvcConfigurer {
        @Override
        public void configurePathMatch(PathMatchConfigurer configurer) {
            UrlPathHelper urlPathHelper = new UrlPathHelper();
            // URI에 ;을 없애지 않도록
            urlPathHelper.setRemoveSemicolonContent(false);
            configurer.setUrlPathHelper(urlPathHelper);
        }
    }


    // Controller.java
    @GetMapping("/event/{id}")
    @ResponseBody
    public Event getEvnet(@PathVariable(required = false) int id, @MatrixVariable String name) {
        Event event = new Event();
        event.setId(id);
        event.setName(name);
        return event;
    }
    
    
    // Test.java
    @Test
    public void getEvent() throws Exception {
        mockMvc.perform(get("/event/1;name=ghsong"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                ;
    }
```
- 참고 : 
    - https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-typeconversion
    - https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-matrixvariables




# 
## 2. 요청 매개변수
- @RequestParam
  - 요청 매개변수에 들어있는 단순 타입 데이터를 메소드 아규먼트로 받아올 수 있다.
```java
    @PostMapping("/events")
    @ResponseBody
    public Event getEvent2(@RequestParam String name) {
        Event event = new Event();
        event.setName(name);
            return event;
        }
    }
```

- 값이 반드시 있어야 한다.
    - required=false 또는 Optional을 사용해서 부가적인 값으로 설정할 수도 있다
```java
  public Event getEvent2(@RequestParam(required = false) String name) {
    // 기본값은 true
    ...
  }
```

  - String이 아닌 값들은 타입컨버전을 지원한다.
  - Map<String, String> 또는 MultiValueMap<String, String>을 사용해서 모든 요청 매개변수를 받아 올 수도 있다.
```java
  public Event getEvent2(@RequestParam Map<String, String> params) {
    // name키로 넘어온 매개변수를 받을 수 있다.
    String name = params.get("name");
    ...
  }
```
  - 해당 애노테이션은 생략해서 사용할 수도 있다.
```java
  public Event getEvent2(String name) {
    ...
  }
```

#
### 폼 서브밋(타임리프)
- 폼을 보여줄 요청 처리
  - GET /events/form
  - 뷰 : events/form.html
  - 모델 : "event", new Event()

```java
    @GetMapping("/events/form")
    @ResponseBody
    public String eventsFrom(Model model) {
        Event event = new Event();
        event.setLimit(50);
        model.addAttribute("event", event);
        return "/events/form";
    }
```
- 타임리프(뷰)
  - @{} : URL 표현식
  - ${} : variable 표현식
  - *{} : selection 표현식
    - 참고 : [Getting started with the Standard dialects in 5 minutes - Thymeleaf](https://www.thymeleaf.org/doc/articles/standarddialect5minutes.html)

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Create New Event</title>
</head>
<body>
    <form action="#" th:action="@{/events/from}" method="post" th:object="${event}">
        <input type="text" title="name" th:field="*{name}"/>
        <input type="text" title="limit" th:field="*{limit}"/>
        <input type="submit" value="Create"/>
    </form>
</body>
</html>
```

```java
    // 뷰 Test Code
    @Test
    public void eventFrom() throws Exception {
        mockMvc.perform(get("/events/form"))
                .andDo(print())
                .andExpect(view().name("/events/form"))
                .andExpect(model().attributeExists("event"))
                ;
    }
```



#
## 3. @ModelAttribute
- @ModelAttribute
  - 여러 곳에 있는 단순 타입 데이터를 복합 타입 객체로 받아오거나 해당 객체를 새로 만들때 사용할 수 있다.
```java
    @PostMapping("/events2")
    @ResponseBody
    public Event getEvents(@ModelAttribute Event event) {
        return event;
    }
    
    // 테스트 코드
    @Test
    public void getEvents2() throws Exception {
        mockMvc.perform(post("/events2")
                    .param("name", "ghsong")
                    .param("limit", "30"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("ghsong"))
                .andExpect(jsonPath("limit").value(30))
                ;
    }
```
  - 여러곳 > URI 패스, 요청 매개변수, 세션 등
```java
    @PostMapping("/events22/name/{name}")
    @ResponseBody
    public Event getEvents2(@ModelAttribute Event event) {
        return event;
    }
    
    // 테스트 코드
    @Test
    public void getEvents22() throws Exception {
        mockMvc.perform(post("/events22/name/ghsong")
                    .param("limit", "30"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("ghsong"))
                .andExpect(jsonPath("limit").value(30))
                ;
    }
```
  - 생략 가능

- 값을 바인딩 할수 없는 경우
  - BindException 발생. 400 에러
    - Integer로 받는 매개변수인데 String이 넘어오는 경우

- 바인딩 에러를 직접 다루고 싶은 경우
  - BindlingResult 타입의 아규먼트에서 바로 오른쪽에 추가
    - 타입이 달라서 발생하는 400에러는 잡아주지만 해당 아규먼트를 받아오지 못한다.

```java
    @PostMapping("/events22/name/{name}")
    @ResponseBody
    public Event getEvents2(@ModelAttribute Event event, BindingResult bindingResult) {
        return event;
    }
```
- 바인딩 이후에 검증 작업을 추가로 하고 싶은 경우
    - @Valid 또는 @Validated 애노테이션을 사용한다.
      - Valid 를 붙이면 바인딩을 정상적으로 되지만 해당 객체의 검증 내용을 확인할 수 있다.
```java
    @PostMapping("/events22/name/{name}")
    @ResponseBody
    public Event getEvents2(@Valid @ModelAttribute Event event, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            System.out.println("=================");
            bindingResult.getAllErrors().forEach(c -> {
                System.out.println(c.toString());
            });
        }
        return event;
    }
```
- 참고 : <https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-modelattrib-method-args>




#
## 4. @Validated
- @Valid 애노테이션에서는 그룹을 지정할 방법이 없지만 @Validated에서는 그룹 클래스를 설정할 수 있다.
```java
@Getter @Setter
public class Event {

    interface ValidateLimit{}
    interface ValidateName{}

    private Integer id;

    @NotBlank(groups = ValidateName.class)
    private String name;

    @Min(value = 0, groups = ValidateLimit.class)
    private Integer limit;
}
```

```java
    @PostMapping("/events22/name/{name}")
    @ResponseBody
    public Event getEvents2(@Validated(Event.ValidateLimit.class) @ModelAttribute Event event, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            System.out.println("=================");
            bindingResult.getAllErrors().forEach(c -> {
                System.out.println(c.toString());
            });
        }
        return event;
    }
```
  - 해당 Validated의 그룹으로 지정한 ValidateLimit으로만 검사한다.
    - 만약 Validated의 그룹을 ValidateName으로 지정하면 에러를 검출하지 못한다.



#
## 5. 폼 서브밋(에러처리)
- 바인딩 에러 발생 시 Model에 담기는 정보 
  - Event
  - BindingResult.event
- 타임 리프 사용시 바인딩 에러 보여주기
  > `<p th:if="${#fields.hasErrors('name')}" th:errors="*{name}">Incorrect date</p>`
 
```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Create New Event</title>
</head>
<body>
    <form action="#" th:action="@{/events}" method="post" th:object="${event}">
        <p th:if="${#fields.hasErrors('name')}" th:errors="*{name}">Incorrect date</p>
        <p th:if="${#fields.hasErrors('limit')}" th:errors="*{limit}">Incorrect date</p>
        <input type="text" title="name" th:field="*{name}"/>
        <input type="text" title="limit" th:field="*{limit}"/>
        <input type="submit" value="Create"/>
    </form>
</body>
</html>
```
- 참고 : [Tutorial: Thymeleaf + Spring](https://www.thymeleaf.org/doc/tutorials/2.1/thymeleafspring.html#field-errors)

- 타임리프에서 목록 보여주기
```html
<a th:href="@{/events/form}">Create New Event</a>
<div th:unless="${#lists.isEmpty(eventList)}">
<ul th:each="event: ${eventList}">
<p th:text="${event.Name}">Event Name</p>
</ul>
</div>
```
  - 참고 : <https://www.thymeleaf.org/doc/tutorials/2.1/thymeleafspring.html#listing-seed-starter-data>

- Post / Redirect / Get 패턴
  - Post 이후 브라우저를 리플래시 하더라도 폼 서브밋이 발생하지 않도록 하는 패턴
  - Redirect를 하여 GET 요청 처리
  - 참고 : [Post/Redirect/Get - Wikipedia](https://en.wikipedia.org/wiki/Post/Redirect/Get)

```java
    @PostMapping("/events3")
    public String getEvents3(@Validated @ModelAttribute Event event,
                             BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "/events/form";
        }

        // db save 처리

        return "redirect:/events/list";
    }

    @GetMapping("/events/list")
    public String getEvents(Model model) {
        // db 에서 조회 처리
        Event event = new Event();
        event.setName("ghsong");
        event.setLimit(50);

        List<Event> eventList = new ArrayList<>();
        eventList.add(event);

        model.addAttribute("eventList", eventList);
        return "/events/list";
    }
```




#
## 6. @SessionAttributes
- 모델 정보를 HTTP 세션에 저장해주는 애노테이션
  - HttpSession을 직접 사용할 수도 있음
```java
    @GetMapping("/events/form")
    public String eventsFrom(Model model, HttpSession httpSession) {
        Event event = new Event();
        event.setLimit(50);
        model.addAttribute("event", event);

        httpSession.setAttribute("event", event);
        return "/events/form";
    }
    
    // test code
    @Test
    public void eventFrom() throws Exception {
        mockMvc.perform(get("/events/form"))
                .andDo(print())
                .andExpect(view().name("/events/form"))
                .andExpect(model().attributeExists("event"))
                .andExpect(request().sessionAttribute("event", notNullValue()))
                ;
    }
```
  - 이 애노테이션에 설정한 이름에 해당하는 모델 정보를 자동으로 세션에 넣어준다.
    - @SessionAttributes에 지정해준 이름으로 model에 넣기만해도 자동으로 session으로 저장해준다.
```java

@Controller
@SessionAttributes({"event","event2"})
public class SampleController {
    ...
    @GetMapping("/events/form")
    public String eventsFrom(Model model) {
        Event event = new Event();
        event.setLimit(50);
        model.addAttribute("event", event);
        return "/events/form";
    }
}
```
  - @ModelAttribute는 세션에 있는 데이터도 바인딩해준다.
  - 여러 화면(또는 요청)에서 사용해야 하는 객체를 공유할 때 사용한다.

- SessionStatus 를 사용해서 세션 처리 완료를 알려줄 수 있다.
  - 폼 처리 끝나고 세션을 비울 때 사용.
```java
    @PostMapping("/events")
    @ResponseBody
    public Event getEvent2(@RequestParam(required = false) String name ,
                           @RequestParam Integer limit,
                           SessionStatus sessionStatus) {
        Event event = new Event();
        event.setName(name);
        event.setLimit(limit);

        sessionStatus.setComplete();
        return event;
    }
```




#
## 7. 멀티 폼 서브밋
```java

@Controller
@SessionAttributes("event")
public class MultiFormController {

    @GetMapping("/events/form/name")
    public String eventsForName(Model model) {
        model.addAttribute("event", new Event());
        return "/events/form-name";
    }

    @PostMapping("/events/form/name")
    public String eventsForNameSubmit(@Validated @ModelAttribute Event event,
                                      BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "/events/form-name";
        }
        return "redirect:/events/form/limit";
    }

    @GetMapping("/events/form/limit")
    public String eventsForLimit(@ModelAttribute Event event, Model model) {
        model.addAttribute("event", event);
        return "/events/form-limit";
    }

    @PostMapping("/events/form/limit")
    public String eventsForLimitSubmit(@Validated @ModelAttribute Event event,
                                       BindingResult bindingResult,
                                       SessionStatus sessionStatus) {
        if(bindingResult.hasErrors()){
            return "/events/form-limit";
        }
        sessionStatus.setComplete();
        return "redirect:/events/list2";

    }

    @GetMapping("/events/list2")
    public String getEvents(Model model) {
        // db 에서 조회 처리
        Event event = new Event();
        event.setName("ghsong");
        event.setLimit(50);

        List<Event> eventList = new ArrayList<>();
        eventList.add(event);

        model.addAttribute("eventList", eventList);
        return "/events/list";
    }

}

//form-limit.html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Create New Event</title>
</head>
<body>
    <form action="#" th:action="@{/events/form/limit}" method="post" th:object="${event}">
        <p th:if="${#fields.hasErrors('limit')}" th:errors="*{limit}">Incorrect date</p>
        limit: <input type="text" title="limit" th:field="*{limit}"/>
        <input type="submit" value="Create"/>
    </form>
</body>
</html>

//form-name.html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Create New Event</title>
</head>
<body>
    <form action="#" th:action="@{/events/form/name}" method="post" th:object="${event}">
        <p th:if="${#fields.hasErrors('name')}" th:errors="*{name}">Incorrect date</p>
        name: <input type="text" title="name" th:field="*{name}"/>
        <input type="submit" value="Create"/>
    </form>
</body>
</html>
```



#
## 8. @SesstionAttribute
- HTTP 세션에 들어있는 값 참조할 떄 사용
  - HttpSession 을 사용할 때 비해 타입 컨버전을 자동으로 지원하기 떄문에 조금 편리함
  - HTTP 세션에 데이터를 넣고 뺴고 싶은 경우에는 HttpSession을 사용
```java
    // 인터셉터 생성
    public class VisitTimeInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

            HttpSession session = request.getSession();
            if(session.getAttribute("visitTime") == null){
                session.setAttribute("visitTime", LocalDateTime.now());
            }
            return true;
        }
    }

    // WebConfig에 등록
    @Configuration
    public class WebConfig implements WebMvcConfigurer {
        ...

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new VisitTimeInterceptor());
        }
    }

    // 컨트롤러에서 사용
    @GetMapping("/events/list2")
    public String getEvents(Model model, @SessionAttribute LocalDateTime visitTime) {
        System.out.println(visitTime);
        // db 에서 조회 처리
        Event event = new Event();
        event.setName("ghsong");
        event.setLimit(50);

        List<Event> eventList = new ArrayList<>();
        eventList.add(event);

        model.addAttribute("eventList", eventList);
        return "/events/list";
    }
```
- @SessionAttributes와는 다르다.
  - @SesstionAttributes는 해당 컨트롤러 내에서만 동작.
    - 즉, 해당 컨트롤러 안에서 다루는 특정 모델 객체를 세션에 넣고 공유할 떄 사용
  - @SessionAttirubute는 컨트롤러 밖(인터셉터 또는 필터 등)에서 만들어 준 세션 데이터에 접근할 때 사용



#
## 9. @RedirectAttributes
- 리다이렉트 할 때 기본적으로 Model에 들어있는 primitive type 데이터는 URI 쿼리 매개변수에 추가된다.
  - 스프링 부트에서는 기본적으로 이 기능이 비활성화 되어있다.

```java
    @PostMapping("/events/form/limit")
    public String eventsForLimitSubmit(@Validated @ModelAttribute Event event,
                                       BindingResult bindingResult,
                                       SessionStatus sessionStatus,
                                       Model model) {
        if(bindingResult.hasErrors()){
            return "/events/form-limit";
        }
        sessionStatus.setComplete();
        model.addAttribute("name", event.getName());
        model.addAttribute("limit", event.getLimit());
        return "redirect:/events/list2";

    }
```
  - Ignore-default-model-on-redirect 프로퍼티를 사용해서 활성화 할 수 있다(application.properties 파일 수정)


    `spring.mvc.ignore-default-model-on-redirect=false`
  
  
- 원하는 값만 리다이렉트 할 때 전달 하고 싶다면 RedirectAttributes에 명시적으로 추가할 수 있다.
- 리다이렉트 요청을 처리하는 곳에서 쿼리 매개변수를 @RequestParam 또는 @ModelAttribute로 받을 수 있다.
  - application.properties 설정 삭제
```java
    @PostMapping("/events/form/limit")
    public String eventsForLimitSubmit(@Validated @ModelAttribute Event event,
                                       BindingResult bindingResult,
                                       SessionStatus sessionStatus,
                                       RedirectAttributes attributes) {
        if(bindingResult.hasErrors()){
            return "/events/form-limit";
        }
        sessionStatus.setComplete();
				
        attributes.addAttribute("name", event.getName());
        attributes.addAttribute("limit", event.getLimit());
				
        return "redirect:/events/list2";
    }
    
    // 뷰
    @GetMapping("/events/list2")
    public String getEvents(@RequestParam String name,
                            @RequestParam Integer limit,
                            Model model,
                            @SessionAttribute LocalDateTime visitTime) {
        System.out.println(visitTime);
        // db 에서 조회 처리

        Event event = new Event();
        event.setName(name);
        event.setLimit(limit);

        Event spring = new Event();
        spring.setName("ghsong");
        spring.setLimit(50);

        List<Event> eventList = new ArrayList<>();
        eventList.add(spring);
        eventList.add(event);

        model.addAttribute("eventList", eventList);

        return "/events/list";
    }
```
#
## 10. Flash Attributes
- 주로 리다이렉트시에 데이터를 전달할 때 사용
	- 데이터가 URI에 노출되지 않는다.
	- 임의의 객체를 저장할 수 있다.
		- 커스텀 객체를 넘길수 있음(ex. evnet 객체)
	- 보통 HTTP 세션을 사용한다.

- 리다이렉트 하기 전에 데이터를 HTTP 세션에 저장하고 리다이렉트 요청을 처리 한 다음 그 즉시 제거한다.

- RedirectAttributes를 통해 사용할 수 있다.

```java
    @PostMapping("/events/form/limit")
    public String eventsForLimitSubmit(@Validated @ModelAttribute Event event,
                                       BindingResult bindingResult,
                                       SessionStatus sessionStatus,
                                       RedirectAttributes attributes) {
        if(bindingResult.hasErrors()){
            return "/events/form-limit";
        }
        sessionStatus.setComplete();
				
				// FlashAttributes 사용
        attributes.addFlashAttribute("newEvent", event);
				
        return "redirect:/events/list2";
    }
    
    // 뷰
    @GetMapping("/events/list2")
    public String getEvents(@ModelAttribute("newEvent") Event event,
                            Model model,
                            @SessionAttribute LocalDateTime visitTime) {
        System.out.println(visitTime);
        // db 에서 조회 처리

        Event spring = new Event();
        spring.setName("ghsong");
        spring.setLimit(50);

        List<Event> eventList = new ArrayList<>();
        eventList.add(spring);
        eventList.add(event);

        model.addAttribute("eventList", eventList);

        return "/events/list";
    }
		
		// Test
		@Test
    public void getEvents() throws Exception {
        Event newEvent = new Event();
        newEvent.setName("ghsong");
        newEvent.setLimit(10);

        mockMvc.perform(get("/events/list2")
                    .sessionAttr("visitTime", LocalDateTime.now())
                    .flashAttr("newEvent", newEvent))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("//p").nodeCount(2))
                ;

    }
```

- xpath
	- ([XPath Syntax](https://www.w3schools.com/xml/xpath_syntax.asp)) 문법
	- ([Free Online XPath Tester / Evaluator - FreeFormatter.com](https://www.freeformatter.com/xpath-tester.html)) Test


#
## 11. MultipartFile
- MultipartFile
	- 파일 업로드시 사용하는 메소드 아규먼트
	- MultipartResolver 빈이 설정 되어야 사용할 수 있다.(스프링 부트 자동 설정이 해줌)
	- POST multipart/form-data 요청에 들어있는 파일을 참조할 수 있다.
	- List<MultipartFile> 아규먼트로 여러 파일을 참조할 수 있다.

```html
//파일 업로드 폼
<div th:if="#{message}">
    <h2 th:text="${message}"/>
</div>

<form method="POST" enctype="multipart/form-data" action="#" th:action="@{/file}">
    File : <input type="file" name="file"/>
    <input type="submit" value="Upload"/>
</form>
```

```java
//파일 업로드 처리 핸들러
@GetMapping("/file")
public String fileUploadForm(Model model) {
		return "files/index";
}

@PostMapping("/file")
public String fileUpload(@RequestParam MultipartFile file,
												 RedirectAttributes attributes) {
		// file save

		String message = file.getOriginalFilename() + " is uploaded";
		attributes.addFlashAttribute("message", message);
		return "redirect:/file";
}

//테스트 코드
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void fileUploadTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "hello file".getBytes());

        this.mockMvc.perform(multipart("/file").file(file))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }
}

```


#
## 12. @ResponseEntity
- 파일 리소스를 읽어오는 방버
	- 스프링 ResourceLoader 사용

- 파일 다운로드 응답 헤더에 설정할 내용
	- Content-Disposition : 사용자가 해당 파일을 받을 때 사용할 파일 이름
	- Content-Type : 어떤 파일인가
	- Content-Length : 얼마나 큰 파일인가

- 파일의 종류(미디어 타입) 알아내는 방법
	- <http://tika.apache.org/>

- ResponseEntity
	- 응답 상태 코드
	- 응답 헤더
	- 응답 본문

```java
    @GetMapping("/file/{filename}")
    public ResponseEntity<Resource> fileDownload(@PathVariable String filename) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + filename);
        File file = resource.getFile();

        Tika tika = new Tika();
        String mediaType = tika.detect(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachement; filename=\"" + resource.getFilename() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, mediaType)
                .header(HttpHeaders.CONTENT_LENGTH, file.length() + "")
                .body(resource);
    }
```
	
- 참고
	- [Getting Started · Uploading Files](https://spring.io/guides/gs/uploading-files/)
	- [Getting Started · Uploading Files](https://spring.io/guides/gs/uploading-files/)
	- [Getting a File's Mime Type in Java \| Baeldung](https://www.baeldung.com/java-file-mime-type)
	

#
## 13. @RequestBody & HttpEntity
- @RequestBody
	- 요청 본문(body)에 들어있는 데이터를 HttpMessageConveter를 통해 변환한 객체로 받아올 수 있다.
	- @Valid 또는 @Validated를 사용해서 값을 검증 할 수 있다.
	- BindingResult 아규먼트를 사용해 코드로 바인딩 또는 검증 에러를 확인할 수 있다.

```java
    @PostMapping("/1")
    public Event createEvent(@RequestBody  Event event) {
        // save event

        return event;
    }
		
    @PostMapping("/3")
    public Event createEvent2(@RequestBody @Valid Event event, BindingResult bindingResult) {
        // save event

        if(bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(objectError -> {
                System.out.println(objectError);
            });
        }

        return event;
    }
```

- HttpMessageConverter
	- 스프링 MVC 설정 (WebMvcConfigurer)에서 설정할 수 있다.
	- configureMessageConverters: 기본 메시지 컨버터 대체
	- extendMessageConverters: 메시지 컨버터에 추가
	- 기본 컨버터
		- WebMvcConfigurationSupport.addDefaultHttpMessageConverters


- HttpEntity
	- @RequestBody와 비슷하지만 추가적으로 요청 헤더 정보를 사용할 수 있다.

```java
    @PostMapping("/2")
    public Event createEvent(HttpEntity<Event> request) {
        // save event
        MediaType contentType = request.getHeaders().getContentType();
        System.out.println(contentType);

        return request.getBody();
    }
```

- 참고
	- https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-requestbody
	- https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-httpentity


#
## 14. @ResponseBody & ResponseEntity
- @ResponseBody
	- 데이터를 HttpMessageConvertor를 사용해 응답 본문 메시지로 보낼 때 사용한다.
	- @RestController 사용시 자동으로 모든 핸들러 메소드에 적용된다.

```java
    @PostMapping("/4")
    @ResponseBody
    public Event createEvent4(@RequestBody Event event) {

        return event;
    }
```


- ResponseEntity
	- 응답 헤더 상태 코드 본문을 직접 다루고 싶은 경우에 사용

```java
    @PostMapping("/5")
    @ResponseBody
    public ResponseEntity<Event> createEvent5(@RequestBody @Valid Event event, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().build();
        }

        //return ResponseEntity.ok(event);
        return new ResponseEntity<Event>(event,HttpStatus.CREATED);
    }
		
    @Test
    public void createEvent5() throws Exception {

        Event event = new Event();
        event.setName("ghsong");
        event.setLimit(-20);

        String json = objectMapper.writeValueAsString(event);

        mockMvc.perform(post("/api/events/5")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }
		
```


- 참고
	- https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-responsebody
	- https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-responseentity


#
## 15. ModelAttribute
- @ModelAttribute의 다른 용법
	- @RequestMapping을 사용한 핸들러 메소드의 아규먼트에 사용하기
	- @Controller 또는 @ControllerAdvice를 사용한 클래스에서 모델 정보를 초기화 할 때 사용한다.
	- @RequestMapping과 같이 사용하면 해당 메소드에서 리턴하는 객체를 모델에 넣어 준다.

- @ModelAttribute 메소드

```java
    @ModelAttribute
    public void subjects(Model model) {
        model.addAttribute("subjects", List.of("study", "seminar", "hobby", "social"));
    }
```

#
## 16. @InitBinder
- 특정 컨트롤러에서 바인딩 또는 검증 설정을 변경하고 싶을 때 사용

```java
    @InitBinder
    public void InitEventBinder(WebDataBinder webDataBinder) {
        // id 컬럼 값 걸러냄
        webDataBinder.setDisallowedFields("id");
    }
```

- 바인딩 설정
	- webDataBinder.setDisallowedFields();

- 포매터 설정
	- webDataBinder.addCustomFormatter();

- Validator 설정
	- webDataBinder.addValidators();


    
