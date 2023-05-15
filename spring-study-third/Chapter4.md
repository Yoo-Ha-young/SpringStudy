# 스프링 시큐리티
- 스프링 시큐리티(Spring Security) 자동 구성
- 커스텀 사용자 스토리지 정의
- 커스텀 로그인 페이지
- CSRF 공격으로부터 방어
- 사용자 파악하기

#### build.gradle 의존성 추가하기
```
implementation 'org.springframework.boot:spring-boot-starter-security'
testImplementation 'org.springframework.security:spring-security-test'
```

의존성을 추가하고 어플리케이션이 시작되면 스프링이 프로젝트의 classpath에 있는 스프링 시큐리티 라이브러리를 찾아 기본적인 보안 구성을 설정해 준다.

```
Using generated security password: 83bf6c66-4abf-41ba-bba0-cad096263f6a

This generated password is for development use only. Your security configuration must be updated before running your application in production.
```
설정 후에 어플리케이션을 실행해주면, 위와 같이 콘솔 창에 자리의 비밀번호와 함께 로그가 뜬다.
![image](https://github.com/Yoo-Ha-young/SpringStudy/assets/116700717/b7a566df-4e9d-4f37-b8a6-b46aa2ffd99d)

그리고 localhost 로 접속하면 http://localhost:8080/login 로 들어가지게 되는데,
sign in에 기본으로 설정되었던 아이디인 user를 쓰고 콘솔 창에 뜬 패스워드를 복사해 붙여 넣어 로그인하면 된다.

보안 스타터를 프로젝트 빌드 파일에 추가만 했을 경우에는 다음의 보안 구성이 기본적으로 적용된다.
- 모든 HTTP 요청 경로가 인증(authentication)되어야 한다.
- 어떤 특정 역할이나 권한이 없다.
- 로그인 페이지가 따로 없다.
- 스프링 시큐리티의 HTTP 기본 인증을 사용해서 인증된다.
- 사용자는 하나만 있으며, 이름은 user다. 비밀번호는 암호화해 준다.

여기서 제대로 구성하기 위해 시큐리티를 구성해 준다.
- 스프링 시큐리티의 HTTP인증 대화상자 대신 로그인 페이지로 인증한다.
- 다수의 사용자를 제공하며, 새로운 클라우드 고객이 사용자로 등록할 수 있는 페이지가 있어야 한다.
- 서로 다른 HTTP 요청 경로마다 서로 다른 보안 규칙을 적용한다.
- 사용자 등록 페이지는 인증이 필요하지 않다.

## 스프링 시큐리티 구성하기

- 기본 구성 클래스
~~~

~~~



