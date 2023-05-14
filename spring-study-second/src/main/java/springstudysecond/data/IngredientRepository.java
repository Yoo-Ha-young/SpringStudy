package springstudysecond.data;

import org.springframework.data.jpa.repository.JpaRepository;

import springstudysecond.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, String>{
	
}
