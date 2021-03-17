package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.bratosz.smartlockers.model.orders.OrderStatus;

public interface OrdersStatusRepository extends JpaRepository<OrderStatus, Long> {
}
