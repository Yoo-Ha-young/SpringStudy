# 모델 데이터:정보 브라우저에 보여주기

#### **스프링에서 지원되는 템플릿**
FreeMarker : spring-boot-starter-freemarker
Groovy 템플릿 : spring-boot-starter-groovy-template
JavaServer Pages(JSP) : 톰캐시나 제티 서블릿 컨테이너 자체에서 제공됨
Mustache : spring-boot-starter-mustache
Thymeleaf : spring-boot-starter-thymeleaf

뷰는 사용시 템플릿을 선택하고 의존성으로 추가한 후 /templates에 템플릿을 작성한다.
그러면 스프링 부트가 선택한 템플릿 라이브러리를 찾아서 스프링 MVC 컨트롤러의 뷰로 사용할 컴포넌트를 자동으로 구성한다.


#### **템플릿 캐싱**
템플릿은 최초 사용될 때 한 번만 파싱(코드 분석)된다. 그리고 파싱된 결과는 추후 사용을 위해 캐시에 저장된다.
이것은 프로덕션에서 어플리케이션을 실행할 때 좋은 기능이다. 
매번 요청을 처리할 때마다 불필요하게 템플릿 파싱을 하지 않으므로 성능을 향상시킬 수 있기 때문이다.

템플릿 캐싱을 비활성화 하기 위해 각 템플릿이 캐싱 속성만 flase로 설정하면 된다.
FreeMarker : spring.freemarker.cache
Groovy 템플릿 : spring.groovy.template.cache
Mustache : spring.mustache.cache
Thymeleaf : spring.thymeleaf.cache

properties 파일에서 각 =false 로 속성값을 설정하면 캐싱 비활성화가 된다.

하지만 스프링부트의 DevTools을 쓰면 개발시점에 모든 템플릿 라이브러리의 캐싱을 비활성화 시켜주므로 따로 지정해주지 않아도된다.
그렇지만 어플리케이션이 실무 운영을 위해 배포될 때는 DevTools 자시이 비활성화되므로 템플릿 캐싱이 활성화될 수 있다.

----

피자 클라우드는 온라인으로 피자를 주문할 수 있는 어플리케이션이다.
피자의 식자재를 보여주는 팔레트를 사용해 창의적으로 피자 토핑을 할 수 있게 한다.

고객 자신이 원하는 피자를 디자인할 때 식자재를 보여주고, 선택할 수 있는 페이지가 피자 클라우드 웹 어플리케이션에 있어야한다.
선택할 수 있는 식자재 내역은 수시로 변경될 수 있다.

## 도메인 설정 : 
해당 어플리케이션의 이해에 필요한 개념을 다루는 영역

- 피자 식자재 속성을 정의하는 도메인 클래스
: 식자재의 내역을 데이터베이스로부터 가져와서 고객이 볼 수 있도록 해당 페이지에 전달되어야 한다.

- 객체 : 
ID, 식자재(고기류, 치즈류, 소스류 등), 고객, 고객의 피자 주문

~~~
package springstudyfirst;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Ingredient {
	private final String id;
	private final String name;
	private final Type type;
	
	public static enum Type{
		WRAP, PROTEIN, VEGGIES, CHEES, SAUCE
	}
	
}
~~~

~~~
package springstudyfirst;

import java.util.List;

import lombok.Data;

@Data
public class Pizza {
	private String name;
	private List<String> ingredients;
}
~~~

## 컨트롤러 설정 : 
스프링 웹에서 데이터를 가져오고 처리하는 것은 컨트롤러의 일,
컨트롤러는 스프링 MVC 프레임워크의 중심적인 역할을 수행한다.

식자재 정보를 가져와서 뷰에 전달하는 스프링 MVC 컨트롤러 클래스 :
- 요청 경로가 /design인 HTTP get 요청 처리
- 식자재의 내역을 생성
- 식자재 데이터의 HTML 작성을 뷰 템플릿에 요청하고 작성된 HTML을 웹 브라우저에 전동

~~~
package springstudyfirst.web;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import springstudyfirst.Ingredient;
import springstudyfirst.Ingredient.Type;
import springstudyfirst.Pizza;

@Slf4j
@Controller
@RequestMapping("/design")
public class DesignPizzaController {
	// id, name, type
	public String showDesignForm(Model model) {
		List<Ingredient> ingredients = Arrays.asList(
				new Ingredient("FLOT", "Flour Tortilla", Type.WRAP),
				new Ingredient("COTO", "Corn Tortilla", Type.WRAP),
				new Ingredient("GRBF", "Ground Beef", Type.PROTEIN),
				new Ingredient("CARN", "Carnitas", Type.PROTEIN),
				new Ingredient("TMTO", "Diced Tomatoes", Type.VEGGIES),
				new Ingredient("LETC", "Lettuce", Type.VEGGIES),
				new Ingredient("CHED", "Cheddar", Type.CHEESE),
				new Ingredient("JACK", "Monterrey", Type.CHEESE),
				new Ingredient("SLSA", "Salsa", Type.SAUCE),
				new Ingredient("SRCR", "Sour Cream", Type.SAUCE)
				);

		Type[] types = Ingredient.Type.values();

		for(Type type : types) {
			model.addAttribute(type.toString().toLowerCase(),
					filterByType(ingredients, type));
		}

		model.addAttribute("pizza", new Pizza());

		return "design"; // 뷰 이름
	}

	private List<Ingredient> filterByType(
			List<Ingredient> ingredients, Type type) {

		return ingredients
				.stream()
				.filter(x->x.getType().equals(type))
				.collect(Collectors.toList());
	}
}
~~~


#### GET 요청 처리
@ReauestMapping :  다목적 요청을 처리(클래스 단위 사용가능)
@GetMapping : HTTP GET 요청 처리
@PostMapping : HTTP POST 요청 처리
@PutMapping : HTTP PUT 요청 처리
@DeleteMapping : HTTP DELETE 요청 처리
@PatchMapping : HTTP PATCH 요청 처리

컨트롤러 메서드에 대한 요청-대응 어노테이션을 선언할 땐 특화된 것을 사용하는게 좋음
→ 경로(또는 클래스 수준의 @RequestMapping에서 경로를 상속받음)를 지정하는 어노테이션과 
처리하려는 특정 HTTP 요청을 지정하는 어노테이션 모두를 각각 선언한다.

대개 기본 경로는 클래스 수준의 @RequestMapping을 사용하고
요청 처리 메서드에는 더 특화된 @GetMapping, @PostMapping 등을 이용한다.


#### showDesignForm() 메서드 구성
식자재를 나타내는 Ingredient 객체를 저장하는 List 생성
식자재의 유형(고기, 치즈, 소스 등)을 List에서 filterByType 메서드로 필터링한 후
showDesignForm()의 인자로 전달되는 Model 객체의 속성으로 추가한다.

##### Model
컨트롤러와 데이터를 보여주는 뷰 사이에서 데이터를 운반하는 객체
Model 객체의 속성에 있는 데이터는 뷰가 알 수 있는 서블릿 요청 속성들로 복사된다.


### 뷰 디자인 : 
브라우저에 보여주는 데이터를 HTML로 나타내는 것은 뷰가 하는 일
: 식재자의 내역을 사용자의 브라우저에 보여주는 뷰 템플릿


* **뷰 템플릿 라이브러리 선택하기**
JSP(JavaServer Pages), Thymeleaf, FreeMarker, Mustache, 그루비(Groovy) 기반 템플릿

빌드파일에 타임리프 의존성을 추가하여
스프링 부트 자동 구성에서 런타임 시에 classpath의 Thymeleaf를 찾아 빈을 자동으로 생성한다.
	
``` <p th:text="${키}">placeholder message</p```
타임리프 템플릿은 요청 데이터를 나타내는 요소 속성을 추가로 갖는 HTML이다. 
키가 요청 속성이고, 이것을 타임리프를 사용해 HTML `<p>`태그로 나타낸다.	
`<p>` 요소의 몸체는 키인 서블릿 요청 속성의 값으로 교체되며, 
`th:text`는 교체를 수행하는 타임리프 네임스페이스 속성이다.
`${}` 연산자는 요청 속성의 값을 사용하라는 것이다.	

~~~
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
	  xmlns:th="http://www.thymeleaf.org">
<head>
	<meta charset="UTF-8">
	<title>Spring Study</title>
	<link rel="stylesheet" th:href="@{/style.css}" />
</head>

<body>
	<h1>Design your Pizza!</h1>
	<img th:src="@{/images/newPizza.jpg}"/>	
~~~
`<body>`태그 맨 앞에는 피자 클라우드 로고 이미지와 함께 `<head>` 태그에 있는 `<link>` 스타일시트를 참조한다.
두가지 모두 `Thymelef @{}` 연산자가 참조되었다. 참조되는 위치인 컨텍스트 상대 경로를 알려주기 위해서이다.	

```	
	<form method="POST" th:object="${pizza}">
	<span class="validationError"
		th:if="${#fields.hasErrors('ingredients')}"
		th:errors="*{ingredients}">Ingredient Error</span>	
```




각 유형의 식자재 마다 `<div>`코드가 반복되어 있다.
		
~~~
		<div class="grid">
		
			<div class="ingredient-group" id="wraps">

				<h3>Designate your wrap:</h3>			
				
				<div th:each="ingredient : ${wrap}">
					<input name="ingredient" type="checkbox" th:value=${ingredient.id}/>
					<span th:text="${ingredient.name}">INGREDIENT</span><br/>
				</div>			
			</div>
~~~			

`th:each`속성은 컬렉션을 반복 처리하며, 해당 컬렉션의 각 요소를 HTML로 나타낸다. 따라서 리스트에 저장된 피자 식자재(ingredient 객체)를 모델 데이터로부터 뷰에 보여준다. wrap 요청 속성에 있는 컬렉션의 각 항복에 대해 하나씩 `<div>`를 반복해 `th:each`속성을 사용하고, 각 반복에서 식자재 항목이 `ingredient`라는 이름의 타임리프 변수와 바인딩된다.
			
```			
			<div class="ingredient-group" id="proteins">
				<h3>Pick your protein:</h3>
				<div th:each="ingredient : ${protein}">
					<input name="ingredient" type="checkbox" th:value=${ingredient.id}/>
					<span th:text="${ingredient.name}">INGREDIENT</span><br/>
				</div>			
			</div>
			
			<div class="ingredient-group" id="cheeses">
				<h3>Choose your cheese:</h3>
				<div th:each="ingredient : ${cheese}">
					<input name="ingredient" type="checkbox" th:value=${ingredient.id}/>
					<span th:text="${ingredient.name}">INGREDIENT</span><br/>
				</div>			
			</div>
			
			<div class="ingredient-group" id="veggies">
				<h3>Determine your veggies:</h3>
				<div th:each="ingredient : ${veggies}">
					<input name="ingredient" type="checkbox" th:value=${ingredient.id}/>
					<span th:text="${ingredient.name}">INGREDIENT</span><br/>
				</div>			
			</div>
			
		</div>
		
		
		<div>
			<h3>Name your pizza creation:</h3>
			<input type="text" th:field="*{name}"/>
			<span th:text="${#fields.hasErroers('name')}">XXX</span>
			<span class="validationError"
				th:if="${#fields.hasErrors('name')}"
				th:errors="*{name}"> Name Error </span>
			<br/>
			
			<button>Submit your Pizza!</button>
		</div>		
	</form>
	
</body>

</html>
```


## 폼 입력 처리하고 검사하기 : 
### ① 폼 제출 처리하기

`form method="POST" th:object="${pizza}">`
뷰(design.html)의 `<form>`태그에서 HTML 메서드 속성이 POST로 설정되어 있다. 

그러나 action 속성이 선언되지 선언되지 않았다. 
이 경우 폼이 제출되면 브라우저가 폼의 모든 데이터를 모아서 폼에 나타난 GET 요청과 같은 경로(/design)로 서버에 HTTP POST 요청을 전송한다. 따라서 이 요청을 처리하는 컨트롤러의 메서드가 있어야 한다.



~~~
	@PostMapping
	public String processDesign(Pizza design) {
		// 이 지점에서 피자 디자인(선택된 식자재 내역)을 저장한다.
		log.info("Processing design: " + design);
		return "redirect:/orders/current";	
	}

~~~
클래스 수준의 @RequestMapping 과 연관하여 @PostMapping 어노테이션이 지정된  processDedign() 메서드는 /design 경로의 POST 요청을 처리함을 나타낸다. 따라서 PIZZA 디자인을 한 사용자가 제출한 정보를 이곳에서 처리한다.

디자인 폼이 제출될 때 메서드의 인자로 전달되는 Pizza 객체의 속성과 바인딩되어 객체를 사용해서 어떤 것이든 원하는 처리를 할 수 있다.



```
@Data
public class Pizza {
	private String name;
	private List<String> ingredients;
}
```
checkbox 요소들이 여러 개 있는데, 이것들 모두 ingredients라는 이름을 갖고 텍스트 입력 요소의 이름은 name인 것을 알 수 있다. 이 필드들은 Pizza 클래스의 ingredients 및 name 속성 값과 바인딩된다.

반환값에서 rediret:가 제일 앞에 붙는데 이것은
상대경로인 /orders/current 로 재접속되어야 한다는 것을 나타낸다.
~~~
package springstudyfirst.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springstudyfirst.Order;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/orders")
public class OrderController {

	@GetMapping("/current")
	public String orderForm(Model model) {
		model.addAttribute("order", new Order());
		return "orderForm";
	}
	
	
	// 제출된 주문을 처리
	@PostMapping
	public String processOrder(Order order) {
		log.info("Order submit: " + order);
		return "redirect:/";
	}
}
~~~


`log.info("Order submit: " + order);` 에서 Submit order 버튼을 누르고 넘기면, log info를 통해서 콘솔탭에 출력이되고, redirect: 를 통해서 다시 초기화면으로 돌아가게된다.


### ② 폼 입력 유효성 검사
생성되는 피자의 정보를 입력하지 않거나, 지정한 범위에서 벗어나거나 유효하지 않은 정보를 입력한다면 처리를 해주어야한다.
이렇게 필드들의 유효성 검사를 if문으로 검사하기에는 코드가 너저분해지고 번거로워지며 코드파악, 디버깅이 어려워질 수 있다.
이 기능은 자바 스프링 빈 유효성 검사(Bean Validation API)를 통해 구현한다.

스프링 부트를 사용하면 유효성 검사 API와 이 API를 구현한 Hibernate(하이버네이트) 컴포넌트가 스프링 부트의 웹 스타터 의존성으로 자동 추가된다.

① 유효성을 검사할 클래스에 검사 규칙을 선언한다.
Pizza 클래스 : 
NotNull 어노테이션을 통해 Null 값은 받지 않는다.
Size 어노테이션으로 최소길이를 정해두고 지켜지지 않았을 경우 message를 뱉는다.
~~~
import java.util.List;
import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class Pizza {
	
	@NotNull
	@Size(min=5, message="Name must be at least 5 characters long")
	private String name;

	@NotNull
	@Size(min=1, message="You must choose at least 1 ingredient")
	private List<String> ingredients;
	
}
~~~

Order 클래스 : 
NotNull 어노테이션을 통해 Null 값은 받지 않고, 지켜지지 않았을 경우 message를 뱉는다.
CreditCardNumber를 통해 속성값이 Luhn 알고리즘 검사에 합격한 유효한 신용 카드 번호여야 한다는 것을 선언해주었다.
이 알고리즘 검사는 사용자의 입력 실수나 고의적인 악성 데이터를 방지해주며, 입력된 신용 카드 번호가 실제로 존재하는 것인지, 또는 대금 지불에 사용될 수 있는지는 검사하지 못한다.(이것 까지 하려면 실시간으로 금융망과 연동해야 함)

Pattern 어노테이션으로 MM/YY형식의 검사를 수행하는데, 정규 표현식으로 패턴을 직접 지정해준다.
Digits 어노테이션으로 입력 값이 정확하게 세자리 숫자인지 검사한다.

message 속성은 사용자가 입력한 정보가 어노테이션으로 선언된 유효성 규칙을 충족하지 못할 때 보여줄 메시지를 속성에 정의한다.

```
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.CreditCardNumber;
import lombok.Data;

@Data
public class Order {
	
	@NotBlank(message="Name is required")
	private String deliveryName;

	@NotBlank(message="Street is required")
	private String deliveryStreet;
	
	@NotBlank(message="City is required")
	private String deliveryCity;

	@NotBlank(message="State is required")
	private String deliveryState;

	@NotBlank(message="Zip code required")
	private String deliveryZip;

	@CreditCardNumber(message="Not a valid credit card number")
	private String creditCardNumber;

	@Pattern(regexp="^(0[1-9]1[0-2])([\\/]([1-9][0-9])",
		message="Must be formatted MM/YY")
	private String creditCardExpiration;
	
	@Digits(integer=3, fraction=0, message="Invalid CVV")
	private String creditCardCVV;
}
```



② 유효성 검사를 해야 하는 컨트롤러 메서드에 검사를 수행한다는 것을 지정한다.
~~~
	@PostMapping
	public String processDesign(@Valid Pizza design, Errors errors) {
		if(errors.hasErrors()) {
			return "design";
		}
		
		// 이 지점에서 피자 디자인(선택된 식자재 내역)을 저장한다.
		log.info("Processing design: " + design);
		return "redirect:/orders/current";	
	}
~~~


```
	@PostMapping
	public String processOrder(@Valid Order order, Errors errors) {
		if(errors.hasErrors()) {
			return "orderForm";
		}
		
		log.info("Order submit: " + order);
		return "redirect:/";
	}
```

에러 상세 내역이 Errors 객체에 저장되어 메서드 밖으로 저장된다. 
if 문에서 errors의 hasErrors()메서드를 호출하여 검사 에러가 있는지 확인한다.
그리고 에러가 있다면 중지하고 뷰 이름을 반환하여 폼이 다시 보이게한다.

③ 검사 에러를 보여주도록 폼 뷰를 수정한다.

~~~
	<h3>Here's how I'll pay...</h3>	
	<label for="creditCardNumber">Credit Card #: </label>
	<input type="text" th:field="*{creditCardNumber}"/>
	<span class="validationError"
		th:if="${#fields.hasErrors('creditCardNumber')}"
		th:errors="*{creditCardNumber}">CC Num error</span>
	<br/>
~~~
`<span>` 요소의 class 속성은 사용자의 주의를 끌기 위한 에러의 명칭(validationError)을 지정하는 데 사용된다. 

그리고 `th:if` 속성에서 이 `<span>`을 보여줄지 말지 결정하고 이때 `fields`속성의 hasErrors() 메서드를 사용해서 creditCardNumber 필드에 에러가 있는지 검사한다. 그리고 만일 있다면 `<span>을 나타낸다.

`th:errors` 속성은 필드를 참조하고 그리고 이 필드에 에러가 있다고 가정하고 `<span>에 사전 지정된 메시지CC Num Error)
를 검사 에러 메시지로 교체한다.


