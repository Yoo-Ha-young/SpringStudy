package springstudythird.web;

//import java.util.Arrays;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import lombok.extern.slf4j.Slf4j;
import springstudythird.Ingredient;
import springstudythird.Ingredient.Type;
import springstudythird.Order;
import springstudythird.Pizza;
import springstudythird.data.IngredientRepository;
import springstudythird.data.PizzaRepository;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignPizzaController {
	
	@ModelAttribute(name = "order")
	public Order order() {
		return new Order();
	}

	
	@ModelAttribute(name = "pizza")
	public Pizza pizza() {
		return new Pizza();
	}

	private final IngredientRepository ingredientRepo;
	private PizzaRepository pizzaRepo;
	
	@Autowired
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
			@ModelAttribute Order order) {
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
