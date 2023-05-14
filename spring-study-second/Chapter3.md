# 데이터베이스 연동시키기

- 스프링 JdbcTemplate
- SimpleJdbcInsert 사용
- 스프링 데이터(Spring Date)를 사용해서 JPA 선언 및 사용


데이터 퍼시스턴스(persistence : 저장 및 지속성 유지)를 추가하며 상용구코드(boilerplate code)를 없애기 위해 jdbc 지원 기능을 사용한다. JPA를 쓰면 데이터 레퍼지터리를 사용할 수 있다.

상용구 코드는 언어의 문법이나 형식 등의 이유로 거의 수정 없이 여러 곳에서 반복적으로 사용해야 하는 코드를 말한다.


	implementation 'org.springframework.boot:spring-boot-starter-jdbc'
	runtimeOnly 'com.h2database:h2'
  
### JDBC 리퍼지터리 정의
- 데이터베이스의 모든 식자재 데이터를 쿼리하여 Igredient 객체 컬렉션에 저장
- id를 사용하여 하나의 Ingredient를 쿼리
- Ingredient 객체를 데이터베이스에 저장

