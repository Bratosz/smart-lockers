package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.orders.OrderStatus;
import pl.bratosz.smartlockers.model.orders.OrderType;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.OrdersStatusRepository;

import java.util.Date;
import java.util.List;

@Service
public class OrderStatusService {
    private OrdersStatusRepository ordersStatusRepository;

    public OrderStatusService(OrdersStatusRepository ordersStatusRepository) {
        this.ordersStatusRepository = ordersStatusRepository;
    }

    public OrderStatus create(OrderType orderType, User user) {
        OrderStatus.OrderStage orderStage = resolveOrderStage(orderType, user);
        return create(orderStage, user);
    }

    public OrderStatus create(OrderStatus.OrderStage orderStage, User user) {
        OrderStatus status = new OrderStatus();
        status.setOrderStage(orderStage);
        status.setUser(user);
        status.setDateOfUpdate(new Date());
        return ordersStatusRepository.save(status);
    }

    private OrderStatus.OrderStage resolveOrderStage(OrderType orderType, User user) {
        if(orderType.equals(OrderType.EMPTY)) {
            return OrderStatus.OrderStage.EMPTY;
        } else {
            return user.getInitialStageForOrders();
        }
    }


    public void delete(List<OrderStatus> orderStatusHistory) {
        for (OrderStatus status : orderStatusHistory) {
            ordersStatusRepository.deleteById(status.getId());
        }
    }
}
