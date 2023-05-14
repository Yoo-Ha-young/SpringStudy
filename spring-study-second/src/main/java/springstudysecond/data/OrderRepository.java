package springstudysecond.data;

import org.springframework.data.jpa.repository.JpaRepository;
import springstudysecond.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{
}
