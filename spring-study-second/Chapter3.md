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

schema.sql
```
create table if not exists Ingredient (
	id varchar(4) not null,
	name varchar(25) not null,
	type varchar(10) not null
);

create table if not exists Pizza (
 	id identity,
 	name varchar(50) not null,
 	createdAt timestamp not null
);

create table if not exists Pizza_Ingredients(
	Pizza bigint not null,
	ingredient varchar(4) not null
);

alter table Taco_Ingredients
	add foreign key (pizza) references Pizza(id);
	
alter table Pizza_Ingredients
	add foreign key (ingredient) references Ingredient(id);
	
create table if not exists Pizza_Order (
	id identity,
	deliveryName varchar(50) not null,
	deliveryStreet varchar(50) not null,
	deliveryCity varchar(50) not null,
	deliveryState varchar(2) not null,
	deliveryZip varchar(10) not null,
	creditCardNumber varchar(16) not null,
	creditCardExpiration varchar(5) not null,
	creditCardCVV varchar(3) not null,
	pacedAt timestamp not null
 );
 
 crate table if not exists Pizza_Order_Pizzas (alter
 	pizzaOrder bigint not null,
 	pizza bigint not null
 );
 
 alter table Pizza_Order_Pizzas
 	add foreign key (pizzaOrder) references Pizza_Order(id);

 alter table Pizza_Order_Pizzas
 	ass foreign key (pizza) references Pizza(id);
```
- Ingredient : 식자재 정보 저장
- Pizza : 사용자가 식자재를 선택하여 생성한 피자 디자인에 관한 정보 저장
- Pizza_Ingredients : Pizza와 Ingredient 테이블 관계를 나타냄. Pizza 테이블의 각 행에 대해 하나 이상의 행(피자를 식자재와 연관시키는)을 포함한다.(하나의 피자에는 하나 이상의 식자재가 포함될 수 있다.)
- Pizza_Order : 주문 정보를 저장
- Pizza_Order_Pizzas: Pizza_Order와 Pizza 테이블 간의 관계를 나타내며, Pizza_Order 테이블의 각 행에 대해 하나 이상의 행(주문을 피자와 연결시키는)을 포함한다.(한건의 주문에는 하나 이상의 피자가 포함될 수 있다.)

### 주문 데이터 추가하기
- update() 메서드 사용
- SimpleJdbcInser 래퍼 클래스 사용

~~~
package springstudysecond.data;

import springstudysecond.Pizza;

public interface PizzaRepository {
	Pizza sva(Pizza design);
}
~~~

```
package springstudysecond.data;
import springstudysecond.Order;

public interface OrderRepository {
	Order save(Order order);
}
```

**구현 인터페이스**

~~~
package springstudysecond.data;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import springstudysecond.*;

@Repository
public class JdbcPizzaRepository implements PizzaRepository {
	
	private JdbcTemplate jdbc;

	public JdbcPizzaRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
	
	@Override
	public Pizza save(Pizza pizza) {
		long pizzaId = savePizzaInfo(pizza);
		pizza.setId(pizzaId);
		
		for(Ingredient ingredient : pizza.getIngredients()) {
			saveIngredientToPizza(ingredient, pizzaId);
		}
		return pizza;
	}
	
	private long savePizzaInfo(Pizza pizza) {
		pizza.setCreatedAt(new Date());
		PreparedStatementCreator psc = 
				new PreparedStatementCreatorFactory(
						"insert intoPizza(name, createdAt) values (?,?)",
						Types.VARCHAR, Types.TIMESTAMP)
				.newPreparedStatementCreator(
						Arrays.asList(pizza.getName(),
								new Timestamp(pizza.getCreatedAt().getTime())));;
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbc.update(psc, keyHolder);
		
		return keyHolder.getKey().longValue();
	}
	
	private void saveIngredientToPizza(Ingredient ingredient, long pizzaId) {
		jdbc.update(
				"insert into Pizza_Ingredients(pizza, ingredient) " +
				"values (?,?)",
				pizzaId, ingredient.getId());
	}
	
	
}
~~~

`save()` : Pizza 테이블에 각 식자재를 저장하는 메서드를 호출한다. ㄱ리고 이 메서드에서 반환된 Pizza ID를 사용해서 피자와 식자재의 연관 정보를 저장하는 saveIngredientToPizza()를 호출한다.

keyHolder : 생성된 ID를 제공하는 것이 바로 키홀더이다. 이엇을 사용하기 위해서는 PreparedStatementCreator도 생성해야한다.
PreparedStatementCreator는 CreatorFactory 객체를 생성하는 것으로 시작하며, 이 객체의 newPrepared StatementCreator()를 호출하며, 이때 PreparedStatementCreator를 생성하며 생성하기 위해 쿼리 매개변수의 값을 인자로 전달한다.

PreparedStatementCreator도 객체가 생성되면 이 객체와 KeyHolder 객체를 인자로 던달해 update()를 호출 할 수 있다.

그리고 update()의 실행이 끝나면 `keyHolder.getKey().longValue()`의 연속 호출로 피자 ID를 반환할 수 있다.

그 다음 save() 메서드로 제어가 복귀된 후 saveIngredientToTaco()를 호출해서 Taco객체의 List에 저장된 각 Ingredient 객체를 반복처리한다.



###  피자 주문 저장하고 주문과 연결시키기
```
@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignPizzaController {
	
	
	// 피자 디자인 저장 및 주문과 연결시키기 위해 Order, Pizza 생성자 생성
	@ModelAttribute(name = "order")
	public Order order() {
		return new Order();
	}

	@ModelAttribute(name = "pizza")
	public Pizza pizza() {
		return new Pizza();
	}

	private final IngredientRepository ingredientRepo;
	private PizzaRepository pizzaRepo; // 피자레파지토리 주입하고 사용
	
	@Autowired                                                          // 피자레파지토리 사용부분
	public DesignPizzaController(IngredientRepository ingredientRepo, PizzaRepository pizzaRepo) {
		this.ingredientRepo = ingredientRepo;
		this.pizzaRepo = pizzaRepo;
	}
	
	@GetMapping
	public String showDesignForm(Model model) {
		
		List<Ingredient> ingredients = new ArrayList<>();
		ingredientRepo.findAll().forEach(i -> ingredients.add(i));
		
		Type[] types = Ingredient.Type.values();
		for (Type type : types) {
			model.addAttribute(type.toString().toLowerCase(),
					filterByType(ingredients, type));
		}
		
		model.addAttribute("pizza", new Pizza());
		
		return "design";
	}

	private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
		// TODO Auto-generated method stub
		return ingredients
				.stream()
				.filter(x -> x.getType().equals(type))
				.collect(Collectors.toList());
	}
	
	
	@PostMapping
	public String processDesign(
			@Valid Pizza design, Errors errors,
			@ModelAttribute Order order) { // 주문클래스 추가
		if(errors.hasErrors()) {
			return "design";
		}
		
		// 피자 디자인(선택된 식자재 내역)을 저장
		Pizza saved = pizzaRepo.save(design);
		order.addDesign(saved);
//		log.info("Processing design: " + design);
		
		return "redirect:/orders/current";
	}
	
}
```
- @SessionArttributes("order")가 추가되고 
: 하나의 세션에서 생성되는 Pizza 객체와 다르게 주문은 **다수의 HTTP 요청에 걸쳐 존재**
그러면 세션에서 계속 보존되면서 다수의 요청에 걸쳐 사용될 수 있다.

- @ModelAttribute(name = "") 
: order()와 pizza()에는 메서드 어노테이션 @ModelAttribute이 추가되었다. Order 객체가 모델에 생성되도록 해준다.
해야 한다.

- processDesign() 메서드 : 디자인을 **실제로 처리(저장)하는 일** 수행된다.
이 메서드에서 Pizza  및 Errors 객체와 더불어 Order 객체도 인자로 받는다. 

Order 매개변수에는 @ModelAttribute 어노테이션이 지정되었다.
**이 매개변수의 값이 모델로부터 전달되어야 한다는 것**과 스프링 MVC가 이 매개변수에 **요청 매개변수를 바인딩하지 않아야 한다는 것**을 나타내기 위해서다.

**전달된 데이터의 유효성 검사를한 후**  processDesign()에서는 주입된 PizzaRepository를 사용해서 피자를 저장한다.
그 다음 세션에 보존된 Order에 Pizza 객체를 추가한다.

~~~
import org.hibernate.validator.constraints.CreditCardNumber;

import lombok.Data;

@Data
public class Order {

	private List<Pizza> pizzas = new ArrayList<>();
...
	
	public void addDesign(Pizza design) {
		this.pizzas.add(design);
	}
}
~~~

사용자가 주문 폼에 입력을 완료하고 제출할 때까지 Order 객체는 세션에 남아있고 데이터베이스에 저장되지 않는다.
주문을 저장하기 위해 ORderController가 Order Repository를 사용할 수 있어야 한다.

## SimpleJdbcInsert를 사용해서 데이터 추가

- 피자를 저장 :  해당 피자의 이름과 생성 시간을 Pizza 테이블에 저장하는 것은 물론이며, 
해당 피자의 id 및 연관된 삭자재들의 id도 Pizza_Ingredients 테이블에 저장한다.
- 주문을 저장 : 주문 데이터를 Pizza_Order 테이블에 저장, 해당 주문의 각 피자에 대한 id도 Pizza_Order_Pizzas 테이블에 저장

기능이 추가되거나 연결이 복잡해질 경우에는 PreparedStatementCreator 보다 **SimpleJdbcInsert**를 사용한다.
(이것은 보다 쉽게 데이터를 추가할 수 있도록 Jdbc를 래핑한 객체임)



## 데이터 타입을 변환해 주는 컨버터 converter 클래스

- 스프링의 컨버터 인터페이스에 정의된 convert() 메서드 : 
우리가 Converter에 지정한 타입 변환이 필요할 떄 conver() 메서드가 자동호출 된다.
어플리케이션에서 String 타입의 식자재 ID를 사용해서 데이터베이스에 저장된 특정 식자재 데이터를 읽은 후 Ingredient 객체로 변환하기 위해 컨버터가 사용된다.
그리고 컨버터로 변환된 Ingredient 객체는 다른 곳에서 List에 저장된다.

```
package springstudysecond.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import springstudysecond.Ingredient;
import springstudysecond.data.IngredientRepository;

@Component
public class IngredientByIdConverter implements Converter<String, Ingredient>{
	
	private IngredientRepository ingredientRepo;
	
	@Autowired
	public IngredientByIdConverter(IngredientRepository ingredientRepo) {
		this.ingredientRepo = ingredientRepo;
	}
	
	@Override
	public Ingredient convert(String id) {
		return ingredientRepo.findById(id);
	}
	
}
```

@Component 어토테이션으로 스프링에 의해 자동 생성 및 주입되는 빈으로 생성된다.
그리고 생성자에 @Autowired 어노테이션으로 지정하여 인터페이스를 구현한 빈(JdbcIngredientRepository)인스턴스가 생성자의 인자로 주입된다.

`Converter<변환할 값의 타입, 변환된 값타입>` 으로 써준다. 
convert() 메서드에서 IngredientRepository 인터페이스를 구현한 Jdbc Ingredient Repository의 메서드 findById()를 호출한다.



# 스프링 데이터 JPA 사용해서 데이터 저장하고 사용
- 스프링 데이터 JPA : 관계형 데이터베이스 JPA 퍼시스턴스
- 스프링 데이터 MongoDB : 몽고 문서형 데이터베이스의 퍼시스턴스
- 스프링 데이터 Neo4 : Neo4j 그래프 데이터베이스의 퍼시스턴스
- 스프링 데이터 레디스 : 레디스 키-값 스토어의 퍼시스턴스
- 스프링 데이터 카산드라 : 카산드라 데이터베이스의 퍼시스턴스
다소 규모가 큰 프로젝트일 때는 다양한 데이터베이스 유형을 사용한 데이터 퍼시스턴스에 초점을 둔다.

스프링 데이터에서는 레퍼지토리 인터페이스를 기반으로 이 인터페이스를 구현하는 레포지토리를 자동 생성해 준다.

~~~
package springstudysecond;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
@Entity
public class Ingredient {
	@Id
	private final String id;
	
	private final String name;
	private final Type type;
	
	public static enum Type{
		WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
	}
}
~~~
- @Entity : Ingredien를 JPA 개체(entity)로 선언하기 위해 해당 어노테이션을 추가
- @Id : id 속성에는 반드시 지정해야 하는 어노테이션으로 이 속성이 데이터베이스의 개체를 고유하게 식별한다는 것을 나타낸다.
- @NoArgsConstructor :  JPA에서 개체가 인자 없는 생성자를 가져야 한다. 따라서 인자 없는 생성자의 사용을 원치 않을땐 access 속성을 AccessLevel.PRIVATE로 설정하여 외부에서는 사용하지 못하게 한다. 그리고 초기화가 필요한 final 속성들이 있으므로 force 속성을 true로 설정한다.



```
package springstudysecond;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
public class Pizza {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Date createdAt;
	
	@NotNull
	@Size(min=5, message="Name must be at least 5 characters long")
	private String name;
	
	@ManyToMany(targetEntity = Ingredient.class)
	@Size(min=1, message="You must choose at least 1 ingredient")
	private List<Ingredient> ingredients;
	
	@PrePersist
	void createdAt() {
		this.createdAt = new Date();
	}
}
```

- @GeneratedValue(strategy = GenerationType.AUTO) : 데이터베이스가 자동으로 생성해주는 ID 값이 사용된다.
- @ManyToMany(targetEntity = Ingredient.class) : Pizza 객체는 많은 재료, Ingredient 객체를 가질 수 있다. 하나의 Ingredient는 여러 Pizza 객체에 포함될 수 있다.
- @PrePersist : createdAt 속성을 현재 일자와 시간으로 설정하는데 사용된다.



~~~
package springstudysecond;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.CreditCardNumber;

import lombok.Data;

@Data
@Entity
@Table(name="Pizza_Order")
public class Order {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Date placedAt;

	@NotBlank(message="Name is required")
	private String deliveryName;
	@NotBlank(message="Street is required")
	private String deliveryStreet;
	@NotBlank(message="City is required")
	private String deliveryCity;
	@NotBlank(message="State is required")
	private String deliveryState;
	@NotBlank(message="Zip code is required")
	private String deliveryZip;
	@CreditCardNumber(message="Not a valid credit card number")
	private String creditCardNumber;
	@Pattern(regexp="^(0[1-9]1[0-2])(\\/)([1-9][0-9])",
			message="Must be formatted MM/YY")
	private String creditCardExpiration;
	@Digits(integer=3, fraction=0, message="Invalid CVV")
	private String creditCardCVV;

	@ManyToMany(targetEntity = Pizza.class)
	private List<Pizza> pizzas = new ArrayList<>();
	
	public void addDesign(Pizza design) {
		this.pizzas.add(design);
	}
	
	@PrePersist
	void placedAt() {
		this.placedAt = new Date();
	}
}
~~~

@Table : Order 개체가 데이터베이스의 Pizza_Order 테이블에 저장되어야 한다는 것을 나타낸다. 어떤 개체 entity에도 사용될 수 있지만, Order의 경우는 이 어노테이션을 지정하지 않으면 JPA가 Order라는 이름의 테이블로 Order 개체를 저장할 것이다. 하지만 SQL의 예약어인 Order로 지정하면 문제가 생기기 때문에 별도로 지정해줄 필요가 있다.

```
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IngredientByIdConverter implements Converter<String, Ingredient>{
	
	private IngredientRepository ingredientRepo;
	
	@Autowired
	public IngredientByIdConverter(IngredientRepository ingredientRepo) {
		this.ingredientRepo = ingredientRepo;
	}
	
	@Override
	public Ingredient convert(String id) {
		Optional<Ingredient> optionalIngredient = ingredientRepo.findById(id);
		return optionalIngredient.isPresent() ?
				optionalIngredient.get() : null;
 	}	
}
```
String 타입의 식자재 ID를 사용해서 데이터베이스에 저장된 특정 식자재 데이터를 읽은 후 Ingredient 객체로 변환하기 위해 컨버터를 사용한다.
Optional로 받는 것은 식자재를 찾지 못했을 때 null이 반환될 수 있으므로 안전한 처리를 위해 변경한다.


