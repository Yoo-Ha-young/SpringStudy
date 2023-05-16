package springstudythird.data;

import org.springframework.data.repository.CrudRepository;

import springstudythird.Pizza;

public interface PizzaRepository extends CrudRepository<Pizza, Long> {
	
}