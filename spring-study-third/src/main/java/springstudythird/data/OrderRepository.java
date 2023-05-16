package springstudythird.data;

import org.springframework.data.repository.CrudRepository;

import springstudythird.Order;

public interface OrderRepository extends CrudRepository<Order, Long> {

}