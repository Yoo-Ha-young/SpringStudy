# 모델 데이터:정보 브라우저에 보여주기
피자 클라우드는 온라인으로 피자를 주문할 수 있는 어플리케이션이다.
피자의 식자재를 보여주는 팔레트를 사용해 창의적으로 피자 토핑을 할 수 있게 한다.

고객 자신이 원하는 피자를 디자인할 때 식자재를 보여주고, 선택할 수 있는 페이지가 피자 클라우드 웹 어플리케이션에 있어야한다.
선택할 수 있는 식자재 내역은 수시로 변경될 수 있다.

### 도메인 설정 : 
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

### 컨트롤러 설정 : 
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

##### * Model
컨트롤러와 데이터를 보여주는 뷰 사이에서 데이터를 운반하는 객체
Model 객체의 속성에 있는 데이터는 뷰가 알 수 있는 서블릿 요청 속성들로 복사된다.


### 뷰 디자인 : 
브라우저에 보여주는 데이터를 HTML로 나타내는 것은 뷰가 하는 일
: 식재자의 내역을 사용자의 브라우저에 보여주는 뷰 템플릿


* **뷰 템플릿 라이브러리 선택하기**
JSP(JavaServer Pages), Thymeleaf, FreeMarker, Mustache, 그루비(Groovy) 기반 템플릿





* 폼 입력 처리하고 검사하기
