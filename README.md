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
    - 예로, postForEntity(url, requestDao, Long.class) : url로 requestDao를 전달하고, ResponseEntity<Long : controller의 반환형>을 반환받는다.
    

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
  
<br>
  
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
  
  
<br>

## 9/4
  
- 앞서, page 119에서 JPA Auditing(날짜 자동 생성)을 위해 main Application에 @EnableJpaAuditing을 사용했다.
  
   - @EnableJpaAuditing을 위해선 최소 하나의 @Entity 클래스가 필요하다. @WebMvcTest는 당연히 없다.
   - 따라서 Application에서 @EnableJpaAuditing을 제거하고, 별도의 config 클래스를 만들어 그 위에 선언한다.
   - 해당 클래스에 추가로 @Configuration을 작성한다. @WebMvcTest에서는 @Configuration을 스캔하지 않는다.
   - 하지만 @WebMvcTest에서 WebSecurityConfigurer 빈은 스캔하게 된다. 그래서 SecurityConfig는 읽었지만(?), 내부의 service를 읽을 수가 없어 에러가 발생한다.
   - 따라서 excludeFilters를 통해 SecurityConfig를 스캔 대상에서 제외시켜줘야 한다.
  
<br>
  
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
  - 구축 후, 파라미터 그룹 생성 -> time zone, character set 변경 -> 생성된 파라미터 그룹을 데이터베이스에 연결
  
- IntelliJ & EC2와 DB 연동하기
  
  - 공통 : 데이터베이스 보안그룹 -> VPC 보안그룹에서 RDS 보안 그룹의 인바운드로 내 IP(IntelliJ를 위한)와 EC2의 보안그룹 ID를 추가한다.
  - IntelliJ : Database탭에 엔드포인트, id, password 입력해서 연결 완료(인텔리제이 기본 버전은 plug-in : database browser 설치)
     - alter database 데이터베이스 명을 통해 character set(문자 집합), collate(데이터베이스에서 검색이나 정렬과 같은 작업을 할 때 사용하는 비교를 위한 규칙의 집합) 변경 가능하다(데이터베이스 전체 특성)
  - EC2 : ssh 접속 후, "sudo yum install mysql"을 통해 mysql 설치한다. 이후 "mysql -u 계정 -p -h host주소'를 통해 RDS로 접속할 수 있다.(show database로 확인!)
  
- EC2 서버에 프로젝트 배포하기
    
  - issue : IntelliJ에서 test all pass 확인 후, ec2에 clone해서 ./gradlew test 결과, test 실패 확인
     -> 문제는 이전에 test/resource 내의 application.properties를 지워버린것. 테스트에서는 구글 로그인 연동 필요 없으므로, id / password를 임의로 설정하고 github에 추가해서 다시 build하니 성공
  - 프로젝트 수정 후 push 했다면, 해당 프로젝트 폴더 안에서 git pull을 통해 최신 push 파일을 가져올 수 있다.
  - 현재 EC2엔 그레이들 설치 안했으나, Gradle task를 수행할 수 있다. 이는 gradlew 때문인데, 그레이들 설치되지 않은 환경 / 버전이 다른 상황에서도 해당 프로젝트에 한해 그레이들을 쓸 수 있도록 지원하는 Wrapper 파일이다. 따라서 별도로 설치할 필요 없다.

<br>

## 9/7
  
- EC2 배포 스크립트 만들기
  
  - 배포 : git clone / pull을 통해 새 버전의 프로젝트를 받음 & Gradle / Maven을 통한 프로젝트 테스트와 빌드 / EC2 서버에서 해당 프로젝트를 실행 및 재실행
     - 배포 자동화를 위해 shell script를 작성한다. 
  - linux 명령어
     - ps(process status) : 현재 실행중인 프로세스 목록 & 상태 보여줌
     - pgrep(ps + grep) : process 상태 보여줌과 동시에 grep을 통해 원하는 정보를 출력할 수 있다. -> pgrep -f : use full process name to match, 즉 프로세스와 매칭되는 Pid를 출력한다.
     - if [ 값1 조건식 값2 ]; then ~ fi : 조건문(https://jink1982.tistory.com/48)
     - if [ -z "$CURRENT_PID" ] ~ : current_pid의 길이가 0이면 참이 되어 ~부분이 실행된다. -n 옵션은 반대로 current_pid 길이가 1이상이면(즉 존재하면) 참이 된다.
     - kill -9 pid : 프로세스 id를 이용하여 프로세스를 강제 종료(저장 안한 데이터 소멸)
     - kill -15 pid(default = termination) : 프로세스 id를 이용해서 프로세스를 정상 종료(TERM 시그널 (기본 옵션), 자신이 하던 작업을 모두 안전하게 종료하는 절차를 밟는다. 메모리상에 있는 데이터와 각종 설정/환경 파일을 안전하게 저장한 후 프로세스를 종료한다.
     - ls -tr $Repository/ | grep jar | tail -n 1 : ls로 -tr, 즉 시간 순으로 우선 정렬한다.(-t는 시간의 역순) 그 리스트에서 "jar"이라는 텍스트를 포함한 문장, 즉 파일이름을 찾고 여러개 일 수 있으므로, tail -n 1을 통해 가장 나중의 jar 파일을 출력한다. 
     - nohup : 애플리케이션 실행자가 터미널 종료해도 애플리케이션 계속 구동되도록 이 명령어를 사용한다.
     - nohup java -jar ~ 2>&1 & : nohup은 실행 시 nohup.out이라는 파일을 생성하고, 뒤의 명령을 수행하면서 발생되는 로그를 기록한다. 2>&1을 통해 표준에러를 표준 출력으로 redirect하므로, 에러도 함께 log(nohup.out)으로 기록된다.
     - * 0:표준입력 / 1:표준출력 / 2:표준에러(출력)
   
  - 스프링 부트의 장점으로, 특별히 외장 톰캣을 설치할 필요 없으며, 내장 톰캣을 사용해서 jar 파일만 있으면 바로 웹 애플리케이션 서버를 실행할 수 있다. 
  

- 외부 Security 파일 등록
  
    - -Dspring.config.location : 스프링 설정 파일 위치를 저장한다. classpath:/가 붙을 경우, jar안에 있는 resource 디렉터리를 기준으로 경로가 생성된다.
  
 
- 스프링 부트 프로젝트로 RDS 접근
  
    - 테이블 생성 : JPA에서 사용될 엔티티 테이블 / 스프링 세션이 사용될 테이블 2가지 종류를 직접 생성한다. 스프링 세션 테이블은 schema-mysql.sql 파일에서 확인할 수 있다.
  
    - 프로젝트 생성 : 자바 프로젝트가 MariaDB에 접근하려면 데이터베이스 드라이버가 필요하다. build.gradle에 mariaDB 드라이버 추가한다.
      - 추가로, application-real.properties 파일을 추가한다. 이는 profile=real인 환경(=스프링의 profile)이 구성된다. 보안 / 로그상 이슈가 될 만한 설정을 모두 제거
      - application-xxx.properties를 가진 이름을 통해 -Dispring.profiles.active=xxx와 같이 properties를 활성화시킬 수 있다.
      - properties 내에 spring.profiles.include를 통해 다른 properties를 함께 사용할 수 있다. 
      - 개별적으로 @configuration이 붙은 클래스에 @Profiles를 통해 각각 다른 빈들로 정의해서 사용할 수도 있다. 이 때는 properties 안의 파일에 spring.profiles.active = Profile 이름을 작성해서 사용하면 된다.
  
    - EC2 설정 : 소셜 로그인 기능을 활성화 한다. 
      - 인스턴스에서 퍼블릭 DNS를 통해 EC2 서버에 접근할 수 있다.(EC2 서버에 자동으로 할당된 도메인)
      - 구글 / 네이버의 로그인 서비스에 EC2의 도메인을 등록하지 않았기 때문에 로그인 기능을 사용할 수 없다.
      - 구글에 EC2 주소 등록 : 웹 콘솔 접속 -> API 및 서비스 -> OAuth 동의 화면에서 http:// 없이 EC2의 퍼블릭 DNS를 등록한다. 이후 사용자 인증정보 탭에 8080/login/oauth2/code/google을 추가하여 승인된 리다이렉션 uri에 등록한다.
      - 네이버에 EC2 주소 등록 : 네이버 개발자 센터 -> 내 애플리케이션 -> API 설정에 들어간다. 서비스 URL에 EC2 퍼블릭 DNS 등록(http:// 같이), 네아로(callback URL)에 전체 주소 등록
      - 서비스 url : 로그인을 시도하는 서비스가 네이버에 등록된 서비스인지 확인하는 항목
      - callback url : 인증된 후 리디렉션 되는 애플리케이션의 경로이다.

- 문제 : 수동 실행되는 Test, 수동 build(Merge 이상이 없는지는 build를 수행해야만 알 수 있다)
  
  

- Travis CI 배포 자동화 : 깃허브에 push 하면 자동으로 Test & build & deploy가 되는 환경 구축해보자.

  - CI(Continuous Integration - 지속적 통합) : VCS 시스템에 push가 되면 자동으로 테스트 & 빌드가 수행되어 안정적인 배포 파일을 만드는 과정 (테스팅 자동화가 가장 중요)
  - CD(Continuous Deployment - 지속적인 배포) : 이 빌드 결과를 자동으로 운영 서버에 무중단 배포까지 진행하는 과정
  
  - Travis CI : 깃허브에서 제공하는 무료 CI 서비스. (회사에서는 Jenkins 사용)
     - 기본 설정은 프로젝트 내에 .travis.yml을 만들어서 관리한다.
     - yml : json에서 괄호를 제거한 것
     - .travis.yml을 작성한 후 master branch에 push 하면, travis에서 자동으로 설정파일을 읽어들이고 build를 진행한다.
  
  
  1. Travis CI와 AWS S3 연동하기
  
     - AWS S3(Simple Storage Service) : 일종의 파일 서버. 이미지 파일 비롯한 정적 파일 관리 & 배포 파일 관리
     - 첫 번째로 Travis CI와 S3를 연동한다. 배포는 AWS CodeDeploy 활용
     - CodeDeploy에는 코드 저장 기능이 없다. 따라서 Travis CI가 빌드한 결과를 받아 CodeDeploy가 가져갈 수 있도록 보관하는 공간이 필요하다. 이를 위해 S3가 필요하다.
     - AWS Key 발급 : 일반적으로 AWS 서비스에 외부 서비스 접근 불가, 따라서 IAM(Identity and Access Management) 서비스를 통해 key를 생성해서 사용한다.
     - IAM을 통해 Travis CI(외부 서비스)가 S3, CodeDeploy(AWS 서비스)에 접근할 수 있도록 권한을 부여하자. IAM에서 엑세스 키와 비밀 엑세스 키를 발급 받아 Travis에서 사용하면 된다.
     - 키를 Travis에 $AWS_ACCESS_KEY와 같은 이름으로 등록한 후, .travis.yml에서 이 값을 사용할 수 있다.
     - before_deploy : 배포 전 설정을 작성한다. CodeDeploy는 jar을 인식 못하므로, 모든 빌드 파일을 zip으로 만든 후 deploy/~.zip과 같은 구조로 만든다.
     - deploy : S3 파일 업로드 혹은 CodeDeploy로 배포 등 외부 서비스와 연동될 행위를 선언한다. 주로 acl(access control list), local_dir(before_deploy에서 생성한 디렉토리)를 설정
     - 여기에서는 Travis CI에서 만든 zip 파일을 'deploy' 폴더에서 가져간다.
  
  2. Travis CI와 S3, CodeDeploy 까지 연동하기
  
     - EC2가 배포 대상이므로, CodeDeploy - EC2간 IAM 역할을 추가해야 한다.
     - 역할 : AWS 서비스에서만 할당할 수 있는 권한 / 사용자 : AWS 서비스 외에 사용할 수 있는 권한(Travis CI) 차이가 있다.
     - 서비스는 EC2(여기에서 사용할꺼기 때문), 정책은 AmazonEC2RoleForAWSCodeDeploy로 설정한다. 이 역할을 EC2의 IAM 역할 연결을 통해 할당한다. 그리고 EC2를 재부팅한다.
  
     - CodeDeploy 에이전트 설치 : CodeDeploy 요청을 받기 위한 에이전트 역할
     - EC2 접속 -> aws s3 cp s3://aws-codedeploy-ap-northeast-2/latest/install .--region ap-northeast-2를 통해 s3에서 codedeploy 에이전트를 다운받을 수 있다.
     - cp에서 인자 2개가 반대로 되면 s3로 업로드하는거니 참고
     - CodeDeploy에서 EC2로 접근하려면 마찬가지로 권한이 필요하다. 마찬가지로 역할을 생성한다. 역할 하나밖에 없어 무조건 그걸로 생성한다. 역할 생성 후 애플리케이션 생성 시 역할 및 배포 그룹 지정한다.
     - appspec.yml : aws codeDeploy 설정, source(destination으로 이동할 대상 지정), destination(source에서 저장된 파일을 받을 위치)
     - .travis.yml : key = 전달할 파일, bundle_time = 압축 확장자
     - 모든 내용 작성 후 프로젝트 커밋 & 푸시 하면, Travis CI 자동 빌드 실행된다. 이후 CodeDeploy에서 배포가 수행되고, 지정된 destination으로 zip이 풀려서 도착하게 된다.
  
     - background에서 돌아가는 nohup -> ps -ef | grep “keyword”를 통해 pid 알아낸 후 termination 시킨다.
     - build.gradle의 springBootVersion으로 인해 내부 dependencies 의존성들이 관리가 된다. 따라서 dependencies에는 따로 버전을 명시하지 말도록 하자..
  
  
<br>

## 12/14 복습

- RunWith(SpringRunner.class) : 테스트 진행 시 JUnit에 내장된 실행자 외에 다른 실행자를 실행시킨다.