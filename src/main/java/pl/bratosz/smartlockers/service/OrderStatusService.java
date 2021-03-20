package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.orders.ClothOrder;
import pl.bratosz.smartlockers.model.orders.OrderStatus;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.OrdersStatusRepository;

import java.util.Date;

@Service
public class OrderStatusService {
    private OrdersStatusRepository ordersStatusRepository;

    public OrderStatusService(OrdersStatusRepository ordersStatusRepository) {
        this.ordersStatusRepository = ordersStatusRepository;
    }

    public OrderStatus create(OrderStatus.OrderStage orderStage, User user) {
        OrderStatus status = new OrderStatus();
        status.setActualStage(orderStage);
        status.setUser(user);
        status.setDateOfUpdate(new Date());
        return ordersStatusRepository.save(status);
    }
}
