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

```
package springstudysecond.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import springstudysecond.Ingredient;

@Repository
public class JdbcIngredientRepository implements IngredientRepository{
	private JdbcTemplate jdbc;
	
	@Autowired
	public JdbcIngredientRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public Iterable<Ingredient> findAll() {
		return jdbc.query("select id, name, type from Ingredient", 
				this::mapRowToIngredient);
	}

	@Override
	public Ingredient findById(String id) {
		return jdbc.queryForObject(
				"select id, name, type from Ingredient where id=?",
				this::mapRowToIngredient, id);
	}

	private Ingredient mapRowToIngredient(ResultSet rs, int rowNum) throws SQLException{
		return new Ingredient(
				rs.getString("id"),
				rs.getString("name"),
				Ingredient.Type.valueOf(rs.getString("type")));
	}
	
}
```
위의 코드는 `implements IngredientRepository`로 인터페이스를 상속받아 구현한 내용이다.
인터페이스를 상속받을 시에 안에 있는 메서드를 구현해줘야한다.

`findAll()` : 객체가 저장된 컬렉션을 반환하는 메서드로 query() 메서드를 사용하며, 두개의 인자를 받는다. 
첫 번째 인자는 쿼리를 수행하는 SQL 명령어이며, 두번째 인사는 스프링의 RowMapper 인터페이스를 구현한 mapRowToIngredient 메서드이다.

`findById(String id)` : 하나의 Ingredient 객체만 반환한다. 따라서 query()볃 대신 JdbcTemplate의 queryForObject() 메서드를 사용한다. 이 메서드는 query() 메서드와 같으며 세번째 인자로 검색할 행의 id를 전달한다. 그러면 이 id가 첫 번쨰 인자로 전달된 SQL에 있는 물음표 대신 교체되어 쿼리에 사용된다.



