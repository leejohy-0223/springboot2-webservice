# springboot2-webservice TIL

이동욱 님의 스프링 부트와 AWS로 혼자 구현하는 웹 서비스를 공부하며 작성한 TIL 입니다.  

## 8/31
- JPaRepository<Entity 클래스, PK 타입>을 상속하면 기본적인 CRUD 메서드가 자동 생성
  - 이 때, @Repository 추가할 필요 없다. 
  - 또한 Entity 클래스와 기본 Entity Repository는 같은 패키지에 묶어두자.
  
- Repository Test에서 @SpringBootTest 사용할 경우 H2 데이터베이스를 자동으로 실행해준다.

- 서비드에서 비즈니스를 처리하는 방식 : 트랜잭션 스크립트
- Domain에서 비즈니스를 처리하는 방식 : Domain-Driven-Development
  - 여기에서는, 서비스 메서드는 트랜잭션과 도메인 간의 순서만 보장해준다. 
  
- Controller와 Service에서는 Entity과 거의 유사한 형태인 Dto를 정의해서 사용한다.
  - 절대로 Entity 클래스를 Request/Response 클래스로 사용해선 안된다.
  - Entity 클래스는 데이터베이스와 맞닿은 핵심 클래스이다.
  - Request/Response용 Dto는 View를 위한 클래스라 정말 자주 변경이 필요하다. 따라서 Dto를 사용해야 한다.
  
- 테스트 관련
  - SpringBootTest + TestRestTemplate를 통해 JPA의 기능까지 통합해서 테스트할 수 있다.(WebMvcTest는 단위테스트)
    - TestRestTemplate 사용하기 위해서 SpringBootTest에 webEnvironment 정의가 필요하다. 여기에서는 Random port를 지정해줬다.
    - TestRestTemplate을 사용해서 @Controller 메서드에 직접 요청을 날리고 응답을 받아올 수 있다.
    - 예로, postForEntity(url, requestDao, Long.class) -> url로 requestDao를 전달하고, ResponseEntity<Long -> controller의 반환형>을 반환받는다.
    
- h2 데이터베이스를 메모리에서 실행 => 직접 접근하려면 웹 콘솔을 사용해야 한다.
  - 옵션 : spring.h2.console.enable=true
  - Application main 실행 -> localhost:8080/h2-console로 접근한다.(jdbc url : jdbc:h2:mem:testdb)
  - 데이터베이스에 직접 데이터 하나 넣은 후, API 요청을 통해 json 형태로 조회 가능하다.(controller가 restController이며, EntityDto를 반환하면 json으로 변환되어 브라우저로 보이게 된다.

- JPA auditing : 등록/수정 시간 자동화 하는 방법
  - BaseEntity(@MappedSupperclass)에 @EntityListeners(AuditingEntityListener.class) 작성
  - BaseEntity에 LocalDateTime을 통해 필드 작성 후, @CreatedDate / @LastModifiedDate 등을 작성해주면 이를 상속받는 엔티티 생성 / 수정 시점에 자동으로 시간을 생성해준다.
  
  
- View Template 사용(Thymeleaf로 바꿔서 진행함)
  - 페이지 로딩 속도 높이기 위해 css는 header / js는 footer에 두어야 함(js 용량 클 수록 body 부분이 실행 늦어지기 때문)
  - bootstrap.js 사용 위해서 jquery 필요, 따라서 부트스트랩보다 먼저 작성
  
- Spring Data Jpa에서 제공하지 않는 메서드는 Repository 내 메서드 정의에 @Query를 통해 native query를 작성할 수 있다.
- 규모가 있는 프로젝트에서 데이터 조회는 PK 조인, 복잡한 조건으로 인해 이러한 Entity 클래스만으로는 처리하기 어려워 조회용(select) 프레임워크를 추가로 사용한다.
  - 예시로 Querydsl, MyBatis, jooq 등이 있다.
  - 등록 / 수정 / 삭제는 Spring Data Jpa를 사용하면 된다.
- @Transactional(readOnly = true) 옵션을 통해 트랜잭션 범위는 유지하되, 조회 기능만 남겨두어 조회 속도를 개선시킬 수 있다.
  - 등록 / 수정 / 삭제 기능이 전혀 없는 서비스 메서드에 사용하자.
  
  
## 9/2
  
1. SecurityConfig(extends WebSecurityConfigurerAdapter)  
  
   - @EnableWebSecurity를 통해 스프링 시큐리티를 활성화한다.
   - configure를 overriding하고, 설정 코드를 작성한다.
   - .userService(customOAuth2UserService) -> 소셜 로그인 성공 시, OAuth2UserService를 구현한 Service를 통해 후속 조치를 진행한다.
     - 리소스 서버(즉, 소셜 서비스들)에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능을 명시할 수 있다.
    
2. OAuth2UserService(Custom)
  
   - 크게는, LoadUser 메서드를 구현하여 OAuth2User를 반환한다. 이 결과로 로그인 기능이 작동한다.
  
   - OAuth2UserRequest : Google에 요청하는 어플리케이션의 요청 값들 포함(registrationId, ClientId, password, token)
     - registrationId : Google / naver / kakao 등 구분용
     - userNameAttributeName : OAuth2 로그인 진행 시 키가 되는 필드 값(PK 같은 의미), 기본은 sub(네이버, 카카오는 지원 안함)
  
   - OAuthUser : 개인 정보 포함
     - OAuthUser.getAttributes -> Map<String, Object> 반환. 각각 개인 정보가 key-value 쌍으로 들어 있다.
  
   - registrationId, userNameAttributeName, oAuth2User.getAttributes()를 통해 User를 save or update 한 후, user 클래스를 Dto로 변환하여 세션에 저장한다.
  
   - 마지막으로 new DefaultOAuth2User에 userRolekey, attributes, attribute key를 넣어 반환한다. 
     - 이 때, userRoleKey는 Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey()))와 같이 만들어진다.
     - Collections.singleton의 결과로 유일무이한 1개의 객체만을 가지는 Set이 반환된다. 해당 set을 대상으로 add, remove, clear가 불가능한데, immutable 하기 때문이다.
     - 참고 : https://stackoverflow.com/questions/31599467/what-is-the-benefit-for-collections-singleton-to-return-a-set-instead-of-a-col
  
  
 
