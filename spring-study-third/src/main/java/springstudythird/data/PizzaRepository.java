package springstudythird.data;

import org.springframework.data.jpa.repository.JpaRepository;

import springstudythird.Pizza;

public interface PizzaRepository extends JpaRepository<Pizza, Long> {
}
