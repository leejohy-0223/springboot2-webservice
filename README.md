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
  
  
## 9/4
  
- 앞서, page 119에서 JPA Auditing(날짜 자동 생성)을 위해 main Application에 @EnableJpaAuditing을 사용했다.
  
   - @EnableJpaAuditing을 위해선 최소 하나의 @Entity 클래스가 필요하다. @WebMvcTest는 당연히 없다.
   - 따라서 Application에서 @EnableJpaAuditing을 제거하고, 별도의 config 클래스를 만들어 그 위에 선언한다.
   - 해당 클래스에 추가로 @Configuration을 작성한다. @WebMvcTest에서는 @Configuration을 스캔하지 않는다.
   - 하지만 @WebMvcTest에서 WebSecurityConfigurer 빈은 스캔하게 된다. 그래서 SecurityConfig는 읽었지만(?), 내부의 service를 읽을 수가 없어 에러가 발생한다.
   - 따라서 excludeFilters를 통해 SecurityConfig를 스캔 대상에서 제외시켜줘야 한다.
  
  
## 9/6
- AWS 환경 구축
  
  - EC2(Elastic Compute Cloud) : AWS에서 제공하는 성능, 용량 등을 유동적으로 사용할 수 있는 서버(리눅스 서버 / 윈도우 서버)
  - AMI(Amazon Machine Image) : EC2 인스턴스를 시작하는 데 필요한 정보를 이미지로 만들어 둔 것, 인스턴스라는 가상 머신에 운영체제 등을 설치할 수 있도록 구워 넣은 이미지라고 생각
  - yum(yellowdog updater modified) : RPM(Redhat Package Manager) 기반의 시스템을 위한 자동 업데이터, 소프트웨어와 같은 패키지 설치 / 삭제 도구 
  - t2 : 요금 타입을 의미하며, micro는 사양을 의미한다.
  - 다른 서비스와 달리 크레딧이란 일종의 CPU를 사용할 수 있는 포인트 개념이 있다. 인스턴스 크기에 따라 정해진 비율로 CPU 크레딧을 계속 받게 된다.
  - 태그 : 웹 콘솔에 표기될 태그인 Name 태그를 등록할 수 있다. 해당 인스턴스를 표현하는 여러 이름으로 사용될 수 있다.(EC2의 이름)
  - ssh/port(22) : AWS EC2에 터미널로 접속할 때를 이야기 한다. 본인 집의 IP를 기본으로 추가한다. 
  - pem 키(비밀 키) : 인스턴스 접근을 위한 비밀 키. 인스턴스 생성의 마지막 단계에서 pem 키를 선택한다. 
  - 인스턴스도 결국 하나의 서버이기 때문에 IP가 존재하는데, 다시 시작할 때마다 IP가 변경되는 것을 막기 위해, 고정 IP(Elastic IP)를 할당한다.

- EC2 서버 접속
  
  1. pem 키를 ./ssh로 옮긴다.
  2. chmod 600으로 pem키의 권한을 변경한다.
  3. ./ssh 디렉토리에 config 파일을 생성한다. 본인이 원하는 Host로 등록한다. Hostname은 Elastic IP 사용
  4. chmod 700으로 config에 실행 권한을 준다.
  5. "ssh config에 등록된 서비스 명"을 통해 EC2에 접속한다.
  
- EC2 초기 설정
  
  - Java 기반 웹 애플리케이션(톰캣, 스프링부트) 작동을 위해 다음을 설정해줘야 한다.
     - Java 설치
        1. sudo yum install y - java(버전 입력)으로 yum을 통해 설치하는 방법
        2. curl -O를 통해 Java release Archive에서 링크로 직접 설치하는 방법(https://hoonmaro.tistory.com/52)
     - 타임존 변경 : 기본 서버 시간은 미국이므로, 한국 시간으로 변경(/etc/localtime에 링크 걸어주기)
     - hostname 변경 & /etc/hosts에 반영 
  
- AWS RDS 구축
  
  - AWS에서는 관리형 서비스인 RDS(Relational Database Service)를 지원한다. 
  - 하드웨어 프로비저닝(사용자 대신 하드웨어 인프라 설정해서 제공), 데이터베이스 설정, 패치 & 작업 등을 자동화 하여 개발자가 개발에만 집중할 수 있도록 한다.
  - MariaDB(프리티어 대상)으로 설정, 이후에 규모가 커지면 Aurora로 교체
