package southside.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import southside.demo.models.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
