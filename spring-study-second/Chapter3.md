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

```
package springstudysecond.data;

import springstudysecond.Ingredient;

public interface IngredientRepository {
	Iterable<Ingredient> findAll();
	Ingredient findById(String id);
	Ingredient save(Ingredient ingredient);
}
```

Ingredient 리퍼지터리가 해야 할일을 IngredientRepository 인터페이스에 정의했으므로 JdbcTemplate을 이용해 데이터베이스 쿼리에 사용할 수 있도록 인터페이스를 구현해줘야 한다.


~~~
package springstudysecond.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcIngredientRepository {
	private JdbcTemplate jdbc;
	
	@Autowired
	public JdbcIngredientRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
}
~~~

리퍼지토리 클래스에 @Repository 어노테이션을 지정하면 스프링 컴포넌트 검색에서 이 클래스를 자동으로 찾아서 스프링 어플리케이션 컨텍스트의 빈으로 생성해준다.

그리고 @Autowired 어노테이션을 통해서 스프링이 해당 빈을 JdbcTemplate에 주입(연결)해준다.




