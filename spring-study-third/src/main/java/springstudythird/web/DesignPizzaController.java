package springstudythird.web;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import springstudythird.Pizza;
import springstudythird.Order;
import springstudythird.Ingredient;
import springstudythird.Ingredient.Type;
import springstudythird.data.IngredientRepository;

import java.security.Principal;
import springstudythird.data.UserRepository;
import springstudythird.User;

import javax.validation.Valid;
import org.springframework.validation.Errors;
import springstudythird.data.PizzaRepository;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
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
	public String showDesignForm(Model model, Principal principal) {
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
	
	private List<Ingredient> filterByType(
			List<Ingredient> ingredients, Type type) {
		return ingredients
				.stream()
				.filter(x -> x.getType().equals(type))
				.collect(Collectors.toList());
	}
	
	@ModelAttribute(name = "order")
	public Order order() {
		return new Order();
	}
	
	@ModelAttribute(name = "pizza")
	public Pizza pizza() {
		return new Pizza();
	}

	@PostMapping
	public String processDesign(@Valid Pizza design, Errors errors, @ModelAttribute Order order) {
		if (errors.hasErrors()) {
			return "design";
		}
		
		Pizza saved = pizzaRepo.save(design);
		order.addDesign(saved);
		
		return "redirect:/orders/current";
	}
}