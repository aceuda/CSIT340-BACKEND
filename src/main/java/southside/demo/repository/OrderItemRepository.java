package southside.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import southside.demo.models.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
