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
 : 한 명 이상의 사용자를 처리할 수 있도록 사용자 정보를 유지/관리하는 사용자 스토어 구성

### 기본 구성 클래스 : SpringSecurity.class

~~~
package springstudythird.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/design", "/orders")
					.access("hasRole('ROLE_USER')")
				.antMatchers("/", "**").access("prmitAll")
			.and()
			.httpBasic();
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("user1")
			.password("{noop}password1")
			.authorities("ROLE_USER")
			.and()
			.withUser("user2")
			.password("{noop}password2")
			.authorities("ROLE_USER");
			
	}
}
~~~
보안 구성 클래스인 WebSecurityConfigurerAdapter의 서브 클래스이다. 그리고 두개의 configure() 메서드를 오버라이딩 하고 있다.
* `protected void configure(HttpSecurity http)` : HTTP 보안을 구성하는 메서드이다.
* `public void configure(AuthenticationManagerBuilder auth)` : 사용자 인증 정보를 구성하는 메서드이다. 위의 사용자 스토어 중 어떤 것을 선택하든 이 메서드에서 구성한다.

*보안을 테스트할 때는 웹 브라우저를 private 또는 incognito 모드로 설정하는 것이 좋다. 구글 크롬의 Incognito 모드(시크릿 모드), IE(인터넷 익스플로러)의 InPrivate 브라우징, 파이어폭스의 Private 브라우징 등을 활성화하면 된다. 이렇게 사용하면 검색 세션에 관한 데이터인 쿠키, 임시 인터넷 파일, 열어 본 페이지 목록 및 기타 데이터를 저장하지 못하도록 한다.


#### 인메모리(in-memory) 사용자 스토어
~~~
@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("user1")
			.password("{noop}password1")
			.authorities("ROLE_USER")
			.and()
			.withUser("user2")
			.password("{noop}password2")
			.authorities("ROLE_USER");
			
	}
~~~
위의 코드는 사용자를 인메모리의 사용자 스토어에 구성하는 방법이다.
사용자 정보를 유지 및 관리할 수 있는 곳 중 하나가 메모리로 변경이 필요 없는 사용자만 미리 정해 놓고 어플리케이션을 실행하면 아예 보안 구성 코드 내부에 정의할 수 있다.

- `inMemoryAuthentication()` : 보안 구성 자체에 사용자 정보를 직접 지정할 수 있는 메서드
- `withUser("사용자 이름")` : 해당 사용자의 구성이 시작된다. 이때 사용자 이름을 인자로 전달한다.
- `password("{noop}비밀번호")` : 인자로 전달되는 것을 비밀번호로 구성하는 메서드로 {noop} 을 지정하여 비밀번호를 암호화하지 않는다.
- `authorities("ROLE_USER")` : 권한을 부여하며 해당 메서드 대신 `.roles("USER")를 사용해도 된다.

인메모리 사용자 스토어는 ㅌ[스트 목적이나 간단한 어플리케이션에서 편리하다. 그렇지만 사용자의 정보의 추가나 변경은 쉽지 않다.
즉, 사용자의 추가, 삭제, 변경이 필요하다면 보안 구성 코드를 변경하고 어플리케이션을 다시 빌드하고 배포 및 설치해야 한다.

#### JDBC 기반 사용자 스토어
사용자 정보는 관계형 데이터베이스로 유지/관리되는 경우가 많다.
관계형 데이터베이스에 유지되는 사용자 정보를 인증하기 위해 JDBC를 사용해 스프링 시큐리티를 구성한다.
```
  import javax.sql.DataSource;
  @Autowired
	DataSource dataSource;
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		// 기존 인메모리 스토어에서 수정
		// 데이터 베이스로 지원되는 사용자 스토어 : JDBC 기반
		auth
			.jdbcAuthentication()
			.dataSource(dataSource);
		
	}
```
- `jdbcAuthentication()` : AuthenticationManagerBuilder에서 해당 메서드를 호출한다. 

- `dataSource()` : 이때 데이터베이스를 액세스하는 방법을 알 수 있도록 해당 메서드를 호출하여 DataSource도 설정해준다.
그리고 선언시에 @Autowired를 지정하여 자동으로 주입되게 해준다.


- 기본 사용자 쿼리를 대체하기
스프링 시큐리티의 사용자 정보 데이터베이스 스키마를 사용할 땐 configure() 메서드의 코드를 쓰면 충분하다.
사용자 정보를 저장하는 테이블과 열이 정해져 있고 미리 생성되어 있기 때문이다. 
즉, 사용자 정보를 찾을 때 스프링 시큐리티의 내부 코드에서는 기본적으로 다음 쿼리를 수행해준다.

~~~
public static final String DEF_USERS_BY_USERNAME_QUERY =
  "select username, password, enabled " +
  "from users " +
  "where username = ?";
  
public static final String DEF_AUTHORITIES_BY_USERNAME_QUERY =
  "select username, authority " +
  "from authorities " +
  "where username = ?";

public static final String DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY =
  "select g.id, g.group, ga.authority " +
  "from authorities g, group_members gm, group_authorities ga " +
  "where gm.username = ? " +
  "and g.id = ga.group_id " +
  "and g.id = gm.group_id";
~~~


이처럼 스프링 시큐리티에 사전 지정된 데이터베이스 테이블과 SQL 쿼리를 사용하려면 관련 테이블을 생성하고 사용자 데이터를 추가해야 한다. 스키마를 정의하고, 데이터를 추가하면 된다.

<스키마 쿼리>
```
drop table if exists users;
drop table if exists authorities;
drop table if exists ix_auth_username;

create table if not exists users (
	username varchar2(50) not null primary key,
	password varchar2(50) not null,
	enabled char(1) default '1');

create table if not exists authorities (
	username varchar2(50) not null,
	authority varchar2(50) not null,
	constraint fk_authorities_users
		foreign key(username) references users(username));
	
create unique index ix_auth_username
	on authorities (username, authority);
```
  
<데이터 쿼리>
~~~
insert into users (username, password) values ('user1', 'password1');
insert into users (username, password) values ('user2', 'password2');

insert into authorities (username, authority)
	values ('user1', 'ROLE_USER');
insert into authorities (username, authority)
	values ('user2', 'ROLE_USER');
	
commit;
~~~


이대로 프로그램을 돌리게 되면 아래와 같이 오류가 뜨게된다.

![image](https://github.com/Yoo-Ha-young/SpringStudy/assets/116700717/cd1cb19c-6430-4caf-a477-8c318d380093)

이유는 password("{noop}비밀번호") 를 사용할때 처럼 {noop}을 지정하여 비밀번호를 암호화하지 않았기 때문이다.

```
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		// 데이터 베이스로 지원되는 사용자 스토어 : JDBC 기반
		auth
			.jdbcAuthentication()
			.dataSource(dataSource)
			.usersByUsernameQuery(
					"select username, password, enabled from users " +
					"where username=?")
			.authoritiesByUsernameQuery(
					"select username, authority from authorities " +
					"where username=?");
		}
``` 
위의 코드에서 사용되는 쿼리에는 테이블의 이름이 스프링 시큐리티 기본 데이터베이스 테이블과 달라도된다. 
테이블이 갖는 열의 **데이터 타입**과 **길이**는 **일치**해야 한다. 

스프링 시큐리티의 기본 SQL 쿼리를 우리 것으로 대체할 떄는 매개변수(where 절에 사용됨)는 하나이고, username이어야 한다.
사용자 정보 인증 쿼리에서는 username, password, enabled 열의 값을 반환해야 한다.
사용자 쿼리에서는 해당 사용자 이름과 부여된 권한을 포함하는 0 또는 다수의 행을 반환할 수 있다.
그리고 그룹 권한 쿼리에서는 각각 그룹 id, 그룹 이름, 권한 열을 갖는 0 또는 다수의 행을 반환할 수 있다.

### passwordEncoder() 메서드 : 암호화된 비밀번호 사용하기
비밀번호가 평범한 텍스트로 저장된다면 보안상 좋지 않고, 비밀번호를 암호화해서 데이터베이스에 저장하면 사용자가 입력한 평범한 텍스트의 비밀번호와 일치하지 않기 때문에 인증에 실패할 것이다. 따라서 **비밀번호를 데이터베이스에 저장할 때와 사용자가 입력한 비밀번호는 모두 같은 암호화 알고리즘을 사용해서 암호화해야 한다.**


**[PasswordEncoder 인터페이스의 구현 클래스]**
- BCryptPasswordEncoder : bcrypt를 해싱 암호화
- NoOpPasswordEncoder : 암호화하지 않음
- Pbkdf2PasswordEncoder : PBKDF2를 암호화
- ScryptPasswordEncoder : scrypt를 해싱 암호화
- StandardPasswordEncoder : SHA-256을 해싱 암호화

<PasswordEncoder 인터페이스>
~~~
public interface PasswordEncoder {
  String encode(CharSequence rawPassword);
  boolean matches(CharSequence rawPassword, String encodedPassword);
}
~~~

어떤 비밀번호 인코더를 사용하든, 일단 암호화되어 데이터베이스에 저장된 비밀번호는 암호가 해독되지 않는다.
대신 로그인 시에 사용자가 입력한 비밀번호와 동일한 알고리즘을 사용해서 암호화된다. 그 다음 데이터베이스의 암호화된 비밀번호화 비교되며, 이것은 PasswordEncoder의 matches() 메서드에서 수행된다.

여기까지 작성해두는 것만으로 정상적으로 로그인이 되지 않는다. 사용자의 비밀번호가 암호화가 되어있기 때문에 사용자가 입력한 값과 암호화된 값이 다른 것으로 간주되어 로그인에 실패되기 때문이다.

*따라서 현재까지 작성한 configure() 메서드가 데이터베이스의 사용자 정보를 읽어서 제대로 인증을 하는지 확인해 보기 외해서는 PasswordEncoder 인터페이스를 구현하되 비밀번호를 암호화하지 않는 클래스를 임시로 작성하고 사용해야한다.

```
import org.springframework.security.crypto.password.PasswordEncoder;

public class NoEncodingPasswordEncoder implements PasswordEncoder{

	@Override
	public String encode(CharSequence rawPwd) {
		return rawPwd.toString();
	}

	@Override
	public boolean matches(CharSequence rawPwd, String encodedPwd) {
		return rawPwd.toString().equals(encodedPwd);
	}
	
}
```
NoEncodingPasswordEncoder 클래스에서는 PasswordEncoder 인터페이스의 encode()와 matches() 메서드를 구현한다. 
- `encode()` : 메서드에서는 로그인 대화상자에서 입력된 비밀번호를 암호화하지 않고 String으로 반환한다.
- `matches()` : encode에서 반환된 비밀번호를 데이터베이스에서 가져온 비밀번호와 비교한다. 여기서는 현재 users 테이블의 password열에 저장된 비밀번호가 암호화되지 않았으므로 결국 암호화되지 않은 두 개의 비밀번호를 비교하는 것이다.


configure(AuthenticationManagerBuilder auth)에서 아래와 같이 수정해준다.
~~~
.passwordEncoder(new NoEncodingPasswordEncoder());
~~~


## 사용자 인증의 커스터마이징 : 커스텀 사용자 명세 서비스
모든 데이터의 퍼시스턴스를 처리하기 위해 스프링 데이터 JPA를 사용했다.
따라서 **사용자 데이터도 또한 방법으로 퍼시스턴스를 처리하는 것이 좋다.**
데이터는 관계형 데이터베이스에 저장될 것으로 JDBC 기반 인증을 사용할 수 있고, 사용자 정보의 저장은 스프링 데이터 레파지토리를 사용하는 것이 좋다.

### 사용자 도메인 객체와 퍼시스턴스 정의
어플리케이션을 사용해 클라우드 고객이 등록할 때는 사용자 이름과 비밀번호 외 전체 이름, 주소, 전화번호 등을 제공한다.
이 정보는 주문 폼에 미리 보여주기 위해 사용되며 이외에 다양한 목적으로도 사용될 수 있다.

- User 클래스
```
package springstudythird;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
@RequiredArgsConstructor
public class User implements UserDetails{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private final String username;
	private final String password;
	private final String fullname;
	private final String street;
	private final String city;
	private final String state;
	private final String zip;
	private final String phoneNumber;
	
	@Override
	public Collection<? extends
			GrantedAuthority> getAuthorities() {
		return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}
}
```
 User 클래스는 UserDetails 인터페이스를 구현하며 기본 사용자 정보를 프레임워크에 제공한다.
 해당 사용자에게 부여된 권한과 해당 사용자 계정을 사용할 수 있는 지의 여부 등이다.
 - `getAuthorities()` : 해당 사용자에게 부여된 권한을 저장한 컬렉션을 반환한다. 메서드 이름이 is로 시작하고 Expired로 끝나는 메서드들은 해당 사용자 계정의 활성화 또는 비활성화 여부를 나타내는 boolean 값을 반환해준다.

여기서는 사용자를 비활성화할 필요가 없기 때문에 is로 시작하고 Expired로 끝나는 메서드들은 모두 true로 사용자가 활정화됨을 나타냄으로 지정했다.
 
#### <User 레파지토리 인터페이스 정의>
~~~
import org.springframework.data.repository.CrudRepository;
import springstudythird.User;

public interface UserRepository extends CrudRepository<User, Long>{
	User findByUsername(String username);
}
~~~
CRUD 연산에 추가하여, findByUsername(String username);를 정의했다. 이 메서드는 사용자 이름 즉 id로 User를 찾기 위해 사용자 명세 서비스에서 사용될 것이다. 인터페이스의 구현체(클래스)를 런타임 시에 자동으로 생성한다. 따라서 이것을 사용하는 명세 서비스를 작성할 준비가 된 것이다.

### 사용자 명세 서비스 생성 : 스프링 시큐리티의 UserDetailsService
```
public interface UserDetailsService {
  UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
```
UserDetailsService 인터페이스의 코드이며, 해당 인터페이스의 메서드에서는 사용자 이름이 인자로 전달이되며
메서드 실행 후 UserDetails 객체가 반환되거나 해당 사용자 이름이 없다면 UsernameNotFoundException을 발생시킨다.

User 클래스에서는 UserDetails를 구현하고 UserRepository에서는 findByUsername() 메서드를 제공하므로, 구현클래스에서 사용해야 하는 모든 것이 준비된 것이다.


**<커스텀 사용자 명세 서비스 클래스>**
~~~
public class UserRepositoryUserDetailsService 
	implements UserDetailsService{
	
	private UserRepository userRepo;
	
	public UserRepositoryUserDetailsService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsername(username);
		if(user != null) {
			return user;
		}
		
		throw new UsernameNotFoundException(
				"'User '" + username + "' not found");
		
	}   

}
~~~

- loadUserByUsername() : 메서드에 주입된 UserRepository 인스턴스의 findByUsername()을 호출하여 User를 찾는다.
절대로 null을 반환되지 않는다는 간단한 규칙이 있다. 따라서 만일 findByUsername()에서 null을 반환하면 Exception을 발생시키며 그렇지 않다면 User가 반환된다.

## 사용자 등록하기
스프링 시큐리티에서는 보안의 많은 관점을 알아서 처리해주기도 한다.
사용자 등록 절차에는 직접 개입하지는 않는다. 이것을 처리하기 위해 스프링 MVC 코드를 작성해준다.

```
package springstudythird.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import springstudythird.data.UserRepository;

@Controller
@RequestMapping("/register")
public class RegistrationController {
	private UserRepository userRepo;
	private PasswordEncoder passwordEncoder;
	
	public RegistrationController(
			UserRepository userRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}
	
	@GetMapping
	public String registerForm() {
		return "registration";
	}
	
	@PostMapping
	public String processRegistration(RegistrationForm form) {
		userRepo.save(form.toUser(passwordEncoder));
		return "redirect:/login";
	}
	
	
}
```
- `/register`의 GET 요청이 registerForm() 메서드에 의해 처리되고 논리 뷰 이름인 registration만 반환한다.
- registration 뷰를 정의하는 Thymeleaf 템플릿을 보여준다.


### 로그인폼
~~~
package springstudythird.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.Data;
import springstudythird.User;

@Data
public class RegistrationForm {
	private String username;
	private String password;
	private String fullname;
	private String street;
	private String city;
	private String state;
	private String zip;
	private String phone;
	
	public User toUser(PasswordEncoder passwordEncoder) {
		return new User(
				username, passwordEncoder.encode(password),
				fullname, street, city, state, zip, phone);
		
	}
}
~~~

해당 클래스에서는 폼 제출이 처리될 때 RegistraionController는 PasswordEncoder 객체를 toUser() 메서드의 인자로 전달한다.
제출된 비밀번호는 이러한 방법으로 암호화된 형태로 저장되며, 향후에 사용자 명세 서비스가 이 비밀번호를 사용해서 사용자를 인증한다.

이후 기본적으로 **모든 웹 요청은 인증이 필요하여 웹 요청의 보안을 처리**해야한다.


## 웹 요청 보안 처리
클라우드 어플리케이션의 보안 요구사항으로 주문하기 전에 사용자를 인증해야 한다. 
홈페이지, 로그인 페이지, 등록 페이지는 인증되지 않는 모든 사용자가 사용할 수 있어야 한다.

보안 규칙을 구성하기 위해서는 SecurityConfig 클래스에 `configure(HttpSecurity)` 메서드를 오버라이딩해야 한다.
- HTTP 요청 처리를 허용하기 전에 충족되어야 할 특정 보안 조건 구성
- 커스텀 로그인 페이지 구성
- 사용자가 어플리케이션의 로그아웃을 할 수 있도록 한다.
- CSRF 공격으로부터 보호하도록 구성

```
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers("/design", "/orders")
					.access("hasRole('ROLE_USER')")
					.antMatchers("/", "/**").permitAll();
	}
```
- `authorizeRequests()` : ExpressiongInterceptUrlRegistry 객체를 반환한다. 이 객체를 사용하면 URL 경로와 패턴 및 해당 경로의 보안 요구사항을 구성할 수 있다. /design /orders 의 요청은 인증된 사용자에게만 허용되어야 한다. 그리고 이외의 모든 다른 요청은 모든 사용자에게 허용한다.

- `antMatchers("/design", "/orders")` : 해당 메서드에서 지정된 경로의 패턴 일치를 검사하므로 먼저 지정된 보안 규칙이 우선적으로 처리된다. 따라서 만일 앞 코드에서 두개의 antMatchers() 순서를 바꾸면 모든 요청의 사용자에게 permitAll() 적용이되어 /design과 /orders의 요청은 효력이 없어진다.

- `access("hasRole('ROLE_USER')")`, `permitAll()` : 요청 경로의 보안 요구를 선언하는 메서드이다.

### 커스텀 로그인 페이지 생성
기본 로그인 페이지를 교체하려면 커스텀 로그인 페이지가 있는 경로를 스프링 시큐리티에 알려줘야 한다.
configure(HttpSecurity) 메서드의 인자로 전달되는 HttpSecurity 객체의 formLogin()을 호출해서 할 수 있다.

~~~
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers("/design", "/orders")
					.access("hasRole('ROLE_USER')")
					.antMatchers("/", "/**").access("permitAll")
					.and().formLogin().loginPage("/login");
	}
~~~

- `and()` : formLogin() 호출 코드 앞에 and() 호출로 인증 구성 코드와 연결시킨다. and() 메서드는 인증 구성이 끝나서 HTTP 구성을 적용할 준비가 되었으며 and()는 새로운 구성을 시작할 때 마다 사용할 수 있다.

- `formLogin()` : 커스텀 로그인 폼을 구성하기 위한 호출이다. 그리고 다음 호출하는 - - -- `loginPage()` : 에는 커스텀 로그인 페이지의 경로를 "/login"으로 지정한다. 그러면 사용자가 인증되지 않아서 스프링 시큐리티가 사용자의 로그인이 필요하다 판단할 때 해당 경로로 연결해준다.


## CSRF 공격 방어(Cross Site Request Forgery) : 크로스 사이트 요청 위조
사용자가 웹사이트에 로그인한 상태에서 악의적인 코드(사이트 간의 요청을 위조하여 공격하는)가 삽입된 페이지를 열면 공격 대상이 되는 웹사이트에 자동으로 폼이 제출되고 이 사이트는 위조된 공격 명령이 믿을 수 있는 사용자로부터 제출된 것으로 판단하게 되어 공격에 노출된다. 공격을 막귀 위해 어플리케이션에서는 폼의 숨김 필드에 넣을 CSRF 토큰을 생성할 수 있다. 그리고 해당 필드에 토큰을 넣은 후 나중에 서버에서 사용한다.
이후에 해당 폼이 제출될 때는 폼의 다른 데이터와 함께 토큰도 서버로 전송된다. 그리고 서버에서는 이 토큰을 원래 생성되었던 토큰과 비교하며, 토큰이 일치하면 해당 요청의 처리가 허용된다. 그러나 일치하지 않는다면 해당 폼은 토큰이 있다는 사실을 모르는 악의적인 웹사이트에서 제출된 것이다.

스프링 시큐리티엔 내장된 CSRF 방어 기능이 있다.
`_csrf`라는 이름의 필드를 어플리케이션이 제출하는 폼에 포함시키면 된다.
`<input type="hidden" name="_csrf" th:value="${_csrf.token}"/> <br/>`
```
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers("/design", "/orders")
					.access("hasRole('ROLE_USER')")
					.antMatchers("/", "/**").access("permitAll")
					.and().formLogin().loginPage("/login")
					.and().logout().logoutSuccessUrl("/")
					.and().csrf();
	}
  ```
  
## 사용자 인지하기
사용자가 로그인되었음을 아는 정도로는 충분하지 않을 때가 있다. 이때 사용자 경험에 맞추기 위해 그들이 누구인지 아는 것도 중요하다.
~~~
@Data
@Entity
@Table(name="Pizza_Order")
public class Order implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private Date placedAt;
	
	@ManyToOne
	private User user;
~~~

Order 개체와 User 개체를 연관 시키기 위해서 Order 클래스에서 getter, setter 속성을 추가시켜줘야한다. 그리고 user 속성의 @ManyToOne 어노테이션은 한 건의 주문이 한명의 사용자에 속한다는 것을 나타낸다. 즉, 한 명의 사용자는 여러 주문을 가질 수 있다.

processOrder() 메서드 : 주문을 저장하는 일 수행
인증된 사용자가 누구인지 결정하고 Order 개체의 setUser()를 호출해 해당 주문을 사용자와 연결하도록 processOrder() 메서드를 수정해야 한다.

- Principal : 객체를 컨트롤러 메서드에 주입
- Authentication : 객체를 컨트롤러 메서드에 주입
- SecurityContextHolder : 보안 컨텍스트를 읽음
- @AutheticationPrincipal :  어노테이션을 메서드에 지정

principall 대신 Authentication 객체를 인자로 받도록 processOrder()를 변경할 수도 있다.

```
@PostMapping
public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus, @Authentication Principal User user) {
	if (errors.hasErrors()) {
		return "orderForm";
	}
		
	order.setUser(user);
	
	orderRepo.save(order);
	sessionStatus.setComplete();
		
	return "redirect:/";
}
```

- `@AuthenticationPrincipal`을 쓰면 타입 변환이 필요 없고 Authetication과 동일하게 보안 특정 코드만 갖는다. User 객체가 processOrder()에 전달되면 해당 주문(Order 객체)에서 사용할 준비가 된 것이다.

- `OrderController 클래스의 orderForm()` : 사용자와 주문을 연관시키는 것에 추가하여 현재 주문을 하는 인증된 사용자의 이름을 주소를 주문 폼에 미리 채워서 보여줄 수 있다면 더욱 편리할 것이다. 그렇다면 사용자가 매번 주문을 할 때 마다 이름과 주소를 다시 입력할 필요가 없다.

~~~	
@GetMapping("/current")
public String orderForm(@AuthenticationPrincipal User user,
		@ModelAttribute Order order) {
		
	if(order.getDeliveryName() == null) {
		order.setDeliveryName(user.getFullname());
	}
		
	if(order.getDeliveryStreet() == null) {
		order.setDeliveryStreet(user.getStreet());
	}

	if(order.getDeliveryCity() == null) {
		order.setDeliveryCity(user.getCity());
	}

	if(order.getDeliveryState() == null) {
		order.setDeliveryState(user.getState());
	}

	if(order.getDeliveryZip() == null) {
		order.setDeliveryZip(user.getZip());
	}
		
	return "orderForm";
}
~~~
orderForm() 메서드에서는 인증된 사용자 User 객체를 메서드 인자로 받아서 해당 사용자의 이름과 주소를 Order 객체에 각 속성에 설정한다. 이렇게 하면 주문의 GET 요청이 제출될 때 해당 사용자의 이름과 주소가 미리 채워진 상태로 주문폼이 전송될 수 있다.


- `DesignTacoController의 생성자와 showDesignForm()`
주문 외에도 인증된 사용자 정보를 활용할 곳이 하다 더 있다. 즉, 사용자가 원하는 식자재를 선택하여 피자를 생성하는 디자인 폼에는 현재 사용자의 이름을 보여준다. 이때 UserRepository의 findByUsername() 메서드를 사용해 현재 디자인 폼으로 작업 중인 인증된 사용자를 찾아야 한다.

```
public class DesignPizzaController {
	
	private final IngredientRepository ingredientRepo;
	private PizzaRepository pizzaRepo;
	private UserRepository userRepo;

	
	@Autowired
	public DesignPizzaController(IngredientRepository ingredientRepo, PizzaRepository pizzaRepo, UserRepository userRepo) {
		this.ingredientRepo = ingredientRepo;
		this.pizzaRepo = pizzaRepo;
		this.userRepo = userRepo;
	}
	
	@GetMapping
	public String showDesignForm(Model model, Principal principal) { // 	Principal : 객체를 컨트롤러 메서드에 주입
		// 매개변수는 컨트롤러의 메서드 Principal에서 사용됩니다 . Spring Security 지원 애플리케이션에서 현재 인증된 사용자를 나타냅니다 
		List<Ingredient> ingredients = new ArrayList<>();
		ingredientRepo.findAll().forEach(i -> ingredients.add(i));
		
		Type[] types = Ingredient.Type.values();
		for (Type type : types) {
			model.addAttribute(type.toString().toLowerCase(),
					filterByType(ingredients, type));
		}
		
		String username = principal.getName();
		User user = userRepo.findByUsername(username);
		model.addAttribute("user", user);
		
		
		return "design";
	}
```
