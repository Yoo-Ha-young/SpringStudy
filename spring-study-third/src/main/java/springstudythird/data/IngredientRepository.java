package springstudythird.data;

import org.springframework.data.jpa.repository.JpaRepository;

import springstudythird.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, String>{
	
}
