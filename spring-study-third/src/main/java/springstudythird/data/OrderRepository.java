package springstudythird.data;

import org.springframework.data.jpa.repository.JpaRepository;
import springstudythird.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{
}
