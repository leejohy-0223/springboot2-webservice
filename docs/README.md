## 12/14 복습

- RunWith(SpringRunner.class) 
    - 테스트 진행 시 JUnit에 내장된 실행자 외에 다른 실행자를 실행시킨다. 
    - 여기에서는 스프링 부트 테스트와 JUnit 사이에 연결자 역할을 한다.
    

- WebMvcTest
    - 여러 스프링 테스트 어노테이션 중, Web(Spring MVC)에 집중할 수 있는 어노테이션이다.
    - 선언할 경우 @Controller, @ControllerAdvice 등을 사용할 수 있다. 그 외는 사용 불가능(@Service, @Component 등)
    - JPA 기능이 동작하지 않는다.  
    

- MockMvc
    - 웹 API를 테스트할 때 사용한다.
    - 스프링 MVC 테스트의 시작점
    - 이 클래스를 통해 HTTP API에 대한 테스트를 할 수 있다.
  

- @RequiredArgsConstruct : final 필드가 포함된 변수에 대한 생성자를 만들어준다.


- assertJ 장점 
    - CoreMatchers와 달리 추가적으로 라이브러리가 필요하지 않는다.
    - 자동 완성이 좀 더 확실하게 지원된다.
  

- jsonPath() : $를 기준으로 필드명을 명시한다. (name을 검증할 때는 $.name으로 검증)


- Spring Data JPA 
    - JPA는 인터페이스로서 자바 표준 명세이다.
    - 인터페이스인 JPA를 사용하기 위해서는 구현체가 필요하다. 대표적으로 Hibernate, Eclipse Link등이 있다.
    - 스프링에서 JPA를 사용할 때는 이 구현체를 직접 다루지는 않는다.
    - 구현체를 좀 더 쉽게 사용하고자 추상화 시킨 Spring Data Jpa라는 모듈을 이용해서 JPA 기술을 다루게 된다.
    - 즉 JPA <- Hibernate <- Spring Data JPA와 같은 관계이며,구현체인 Hibernate를 한 단계 더 감싸서 사용했다고 보면 된다.
    - 구현체 교체의 용이성, 저장소 교체의 용이성으로 인해 이러한 방식으로 사용하는 것이다.
    - 구현체 교체 용이 : Spring Data JPA 내부에서 구현체 매핑을 지원해주기 때문에, 구현체 교체가 용이하다.
    - 저장소 교체 용이 : 관계형 데이터베이스 외의 다른 저장소로 쉽게 교체하기 위함이다. 
       - 서비스 초기에는 관계형 DB로 모든 기능을 처리하지만, 트래픽이 많아지면 RDB로는 감당이 안될 수가 있다.
       - 이 때 개발자는 의존성을 Spring data JPA -> Spring Data MongoDB로 의존성만 교체하면 된다.
       - Spring Data의 하위 프로젝트는 기본적인 CRUD 인터페이스가 같기 때문에 가능하다. save(), findAll(), findOne()등의 기본 인터페이스를 가지고 있다.
       - 따라서 저장소가 교체되어도 기본적인 기능은 변함이 없다.
    - 이러한 장점으로 인해 Hibernate를 직접 쓰기보다는 Spring Data 프로젝트를 권장하고 있다.
  

- spring-boot-starter-data-jpa : 스프링 부트용 Spring Data Jpa 추상화 라이브러리이다. 스프링 부트 버전에 맞춰 자동으로 JPA 관련 라이브러들의 버전을 관리한다.
  

- h2 : 인메모리 관계형 DB. 별도 설치 필요 없이 프로젝트 의존성만으로 관리 가능. 메모리에서 실행되기 때문에 재시작마다 초기화 된다는 점을 이용, 테스트 용도로 많이 사용된다. 


- @GeneratedValue : PK의 생성 규칙을 나타낸다. 
    - 스프링 부트 2.0에서는 GenerationType.Identity 옵셥을 통해 auto increment를 적용할 수 있다.
    - Hibernate 5부터 MySQL에서의 GenerationType.AUTO는 IDENTITY가 아닌 TABLE을 기본 시퀀스 젘략으로 가져간다.

  
- 따로 지정 안하더라도 @Entity 적용된 클래스의 필드는 모두 @Column이 적용된다. 기본값 외에 추가로 변경이 필요한 옵션이 있을 경우 명시해서 변경한다.


- @Builder : 해당 클래스의 빌더 패턴 클래스를 생성. 생성자 상단에 선언하면 생성자에 포함된 필드만 빌더에 포함된다.


- JPA의 Entity는 기본 생성자를 가져야 한다. (파라미터가 없는 생성자 필요, public, protected여야 한다.)
    - 이유는 setter의 제한을 위해 정적 팩토리 메서드의 도입에서 시작되었다.
    - 정적 팩토리 메서드를 이용해 private으로 생성자를 처리했다. 하지만 이렇게 하면 Entity 클래스에 오류가 발생한다.
    - Java Reflection API : 구체적인 클래스 타입을 알지 못해도 그 클래스의 메소드, 타입, 변수에 접근할 수 있도록 하는 Java API
         - 이를 통해 컴파일 시점이 아닌 런타임 시점에 동적으로 클래스를 객체화 하여 분석 및 추출해낼 수 있는 프로그래밍 기법이다.


- Reflection API 
    - 자바는 컴파일러를 사용한다. 즉 컴파일 타임에 타입이 결정된다. 예를 들어 Object 참조자에 하위 클래스를 할당했다고 가정하면, 해당 객체는 컴파일 타임에 Object로 결정됐기 때문에 Object 클래스의 인스턴스 변수와 메서드만 사용할 수 있다.
    - 생성된 Obj 객체는 Object 클래스라는 타입만 알 뿐, Car 클래스라는 구체적인 타입은 모른다. 결국 컴파일러가 있는 자바는 구체적 클래스를 모르면 해당 클래스의 정보에 접근할 수 없다.
    - 이러한 불가능한 일을 가능하게 해주는 것이 Reflection API이다.
    - 이를 이용해 구체적인 클래스의 타입을 모르더라도 구체 메서드에 접근할 수 있다. 클래스의 이름만으로 해당 클래스의 정보를 가져올 수 있다.
    - How ?
       - 자바에서는 JVM이 실행되면 사용자가 작성한 자바 코드가 컴파일러를 거쳐 바이트 코드로 변환 후 static 영역에 저장된다. Reflection API는 이 정보를 활용한다.
       - 따라서 클래스 이름만으로 언제든 static 영역을 뒤져서 정보를 가져올 수 있다.
    - 활용 ?
       - 실제 직접 활용할 일은 거의 없다. 컴파일 타임이 아닌 런타임에 동적으로 타입을 분석하고 정보를 가져오므로 JVM을 최적화할 수 없다.
       - 또한 내부가 강제로 노출되기 때문에 추상화가 깨져 이로 인한 부작용이 발생하게 된다.
       - 결론적으로 애플리케이션 개발보다는, 프레임워크, 라이브러리에 사용된다. 사용자가 어떤 클래스를 만들지 예측할 수 없기 때문에 이를 동적으로 해결하기 위함이다. 
       - Spring : BeanFactory에서 사용된다. Bean은 애플리케이션이 실행한 후 런타임에 객체가 호출될 때 동적으로 객체의 인스턴스를 생성한다. 이 때 사용할 수 있다.
       - Spring Data JPA : Reflection API로 가져올 수 없는 정보 중 하나가 생성자의 인자 정보이다. 따라서 기본 생성자가 반드시 있어야 객체를 생성할 수 있다. 기본 생성자로 객체를 생성만 하면, 필드 값은 Reflection으로 넣을 수 있다.
       - 지연 로딩으로 설정 시, 매핑한 Entity를 사용할 때 조회된다. 이 때 해당 Entity에 대해 hibernate는 임시로 proxy 객체를 생성한다. 
       - 이러한 proxy 객체는 대상이 되는 Entity를 상속하기 때문에, public 또는 protected 기본 생성자를 필요로 하게 된다. 이게 없다면 Proxy를 생성할 수 없을 것이다.
       - 안정성 측면에서 좀 더 작은 scope의 protected 생성자를 활용하자.
    - 결론 : 어려운게 아니다. 단지 '프레임워크에서 구체적이지 않은 객체를 받아서 동적으로 해결해주는구나'로 이해하자.
  

<Spring의 웹 계층>
- Web Layer : 컨트롤러, 뷰 템플릿, 필터, 인터셉터 등의 영역이다. 외부 요청과 응답에 대한 전반적인 영역을 이야기한다.
- Service Layer : @Service에 사용되는 서비스 영역. 일반적으로 Controller, Dao 중간 영역에서 사용된다. @Transaction이 사용되어야 하는 영역이다.(트랜잭션, 도메인 간 순서 보장)
- Repository Layer : Database와 같이 데이터 저장소에 접근하는 영역이다. DAO로 이해하면 쉽다.
- Dtos : Dto는 계층 간에 데이터 교환을 위한 객체를 이야기 하며, Dtos는 이들의 영역을 이야기 한다.
- Domain Model : 도메인이라 불리는 개발 대상을 모든 사람이 동일한 관점에서 이해할 수 있고 공유할 수 있도록 단순화 시킨 모델. @Entity가 사용된 영역을 도메인이라고 생각하면 된다.
    - 단, 무조건 데이터베이스의 테이블과 관계가 있어야 하는것은 아니다. VO(Value Object)와 같은 값 객체들도 이 영역에 해당하기 때문이다.
    
- 위에서 비즈니스 처리는 Domain에서 한다. (기존에 서비스로 처리하는 방식을 트랜잭션 스크립트라고 한다.) 정보를 가장 잘 아는 도메인에서 각각 비즈니스 로직 메시지를 받아 처리할 수 있도록 한다.

- @RunWith는 Junit5 부터는 안써도 된다.

- TestRestTemplate 
    - REST 방식으로 개발한 API의 Test를 최적화 하기 위해 만들어진 클래스이다. 
    - 스프링 부트 테스트에서는 Mock Test 뿐 아니라, RestTestTemplate을 이용한 테스트 또한 제공한다.
    - MockTesting과의 차이는 실제 서블릿 컨테이너 실행 여부이며, RestTestTemplate는 서블릿 컨테이너를 직접 실행한다. 
    - 열려있는 포트가 없다면 TestRestTemplate를 빈으로 등록할 수 없다. 따라서 랜덤 포트를 사용한다는 속성을 @SpringBootTest에 지정해줘야 한다.
    - 즉 이를 활용해서 Jpa 기능도 한꺼번에 테스트할 수 있다. 
    - 이후에 Spring Security 적용 시 로그인이 필요하므로 정상동작 하지 않게될 것이다. 따라서 이 때는 mock을 활용한다.
    - testRestTemplate.exchange() : update 시 주로 사용된다. 결과를 ResponseEntity로 반환받으며, Http header 변경이 가능하다.
    

- @MappedSuperclass : JPA Entity 클래스들이 해당 어노테이션이 적용된 엔티티를 상속할 경우 필드들도 칼럼으로 인식하도록 한다.
- @EntityListeners(AuditingEntityListener.class) : 해당 클래스에 Auditing 기능을 포함시킨다.  
- @EnableJpaAuditing을 통해 JPA Auditing 기능을 활성화한다.

- Transaction 
    - 데이터베이스의 상태를 변경하는 작업 또는 한번에 수행되어야 하는 연산을 의미한다.
    - begin, commit을 자동으로 수행해주며, 예외 발생 시 rollback 처리를 자동으로 해준다.
    - 트랜잭션은 4가지의 성질을 가진다.
        1. 원자성 : 한 트랜잭션 내에서 실행한 작업들은 하나의 단위로 처리한다. 즉 모두 성공 혹은 실패
        2. 일관성 : 트랜잭션은 일관성 있는 데이터베이스 상태를 유지한다.(data integrity 만족)
        3. 격리성 : 동시에 실행되는 트랜잭션들이 서로 영향을 미치지 않도록 격리해야 한다.
        4. 영속성 : 트랜잭션을 성공적으로 마치면 결과가 항상 저장되어야 한다.
    - 스프링에서는 어노테이션 방식(선언적 트랜잭션)을 통해 처리를 할 수 있다. 적용된 범위에서는 트랜잭션 기능이 포함된 프록시 객체가 생성되며, 자동으로 commit, rollback을 진행해준다.
    - 스프링은 default option으로 RuntimeException, Error를 두고, CheckedException(예상할 수 있는 예외)는 rollback 되지 않도록 해두었다.
    - checkedException의 경우, 반드시 처리되어야 하는 예외이다. 이를 throw 한다는 것은 이걸 호출하는 코드에서 처리해줘야함을 의미한다. 또한 해당 트랜잭션 안에서 예외 처리가 되었다면 이를 롤백시킬 이유가 없다.
    


