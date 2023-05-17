package springstudysecond.data;

import org.springframework.data.jpa.repository.JpaRepository;

import springstudysecond.Pizza;

public interface PizzaRepository extends JpaRepository<Pizza, Long> {
}
