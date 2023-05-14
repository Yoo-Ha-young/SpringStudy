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
	
	
	@Override
	public Ingredient save(Ingredient ingredient) {
		jdbc.update(
				"insert into Ingredient(id, name, type) values (?,?,?)",
				ingredient.getId(),
				ingredient.getName(),
				ingredient.getType().toString());
		return ingredient;
	}
}
```
위의 코드는 `implements IngredientRepository`로 인터페이스를 상속받아 구현한 내용이다.
인터페이스를 상속받을 시에 안에 있는 메서드를 구현해줘야한다.

`findAll()` : 객체가 저장된 컬렉션을 반환하는 메서드로 query() 메서드를 사용하며, 두개의 인자를 받는다. 
첫 번째 인자는 쿼리를 수행하는 SQL 명령어이며, 두번째 인사는 스프링의 RowMapper 인터페이스를 구현한 mapRowToIngredient 메서드이다.

`findById(String id)` : 하나의 Ingredient 객체만 반환한다. 따라서 query()볃 대신 JdbcTemplate의 queryForObject() 메서드를 사용한다. 이 메서드는 query() 메서드와 같으며 세번째 인자로 검색할 행의 id를 전달한다. 그러면 이 id가 첫 번쨰 인자로 전달된 SQL에 있는 물음표 대신 교체되어 쿼리에 사용된다.
s
`save(Ingredient ingredient)` : update() 메서드로 결과 세트의 데이터를 객체로 생성할 필요가 없으므로 query()나 queryForObject()보다 훨씬 간단하다. update() 메서드에는 수행될 SQL을 포함하는 문자열과 쿼리 매개변수에 지정할 값만 인자로 전달한다.
여기서 3개의 매개변수를 가지고, sava() 메서드의 인자로 전달되는 식자재 객체의 id, name, type 속성의 값이 각 매개변수에 지정된다.

# 스키마 정의 및 데이터 추가
Ingredient 테이블 외에도 주문 정보와 피자 디자인(식자재 구성) 정보를 저장할 테이블들이 필요하다.
테이블들의 곽 관계를 맺어줘야한다.

![PizzaER](https://github.com/Yoo-Ha-young/SpringStudy/assets/116700717/ba03e5b8-0bd3-48d9-adf1-cf3c08a1d6f0)

- Ingredient : 식자재 정보 저장
- Pizza : 사용자가 식자재를 선택하여 생성한 피자 디자인에 관한 정보 저장
- Pizza_Ingredients : Pizza와 Ingredient 테이블 관계를 나타냄. Pizza 테이블의 각 행에 대해 하나 이상의 행(피자를 식자재와 연관시키는)을 포함한다.(하나의 피자에는 하나 이상의 식자재가 포함될 수 있다.)
- Pizza_Order : 주문 정보를 저장
- Pizza_Order_Pizzas: Pizza_Order와 Pizza 테이블 간의 관계를 나타내며, Pizza_Order 테이블의 각 행에 대해 하나 이상의 행(주문을 피자와 연결시키는)을 포함한다.(한건의 주문에는 하나 이상의 피자가 포함될 수 있다.)



