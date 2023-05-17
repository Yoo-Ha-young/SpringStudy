package springstudythird.data;

import org.springframework.data.repository.CrudRepository;

import springstudythird.Ingredient;

public interface IngredientRepository extends CrudRepository<Ingredient, String> {
	
}