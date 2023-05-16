package springstudythird.data;

import org.springframework.data.repository.CrudRepository;

import springstudythird.*;

public interface IngredientRepository extends CrudRepository<Ingredient, String> {
	
}