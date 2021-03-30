package pl.bratosz.smartlockers.service.managers;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothActualStatus;
import pl.bratosz.smartlockers.model.orders.*;
import pl.bratosz.smartlockers.model.orders.OrderStatus.OrderStage;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteOrderParameters;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteForExchangeAndRelease;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteForRelease;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.OrdersRepository;
import pl.bratosz.smartlockers.service.OrderStatusService;
import pl.bratosz.smartlockers.service.managers.creators.OrderCreator;

import java.util.List;
import java.util.function.Consumer;

import static pl.bratosz.smartlockers.model.orders.OrderStatus.OrderStage.*;

@Service
public class OrderManager  {
    private OrderStatusService orderStatusService;
    private OrdersRepository ordersRepository;

    public OrderManager(OrderStatusService orderStatusService, OrdersRepository ordersRepository) {
        this.orderStatusService = orderStatusService;
        this.ordersRepository = ordersRepository;
    }

    public ClothOrder createOne(CompleteForRelease parameters, User user) {
        return OrderCreator.create(parameters, user);
    }

    public ClothOrder createOne(CompleteForExchangeAndRelease parameters, User user) {
        return OrderCreator.createWithExchange(parameters, user);
    }

    public ClothOrder createForExistingCloth(
            OrderType orderType,
            Cloth cloth,
            User user) {
        OrderStatus orderStatus = orderStatusService.create(orderType, user);
        CompleteForRelease parameters = CompleteOrderParameters.createOrderForExistingCloth(
                cloth,
                cloth.getEmployee(),
                orderType,
                orderStatus,
                user);
        return create(parameters);
    }

    private ClothOrder create(
            CompleteForRelease parameters
    ) {
        ClothOrder order = new ClothOrder();
        order.setOrderType(parameters.getOrderType());
        order.setClothToRelease(parameters.getClothToRelease());
        order.setOrderStatus(parameters.getOrderStatus());
        return ordersRepository.save(order);
    }


    public ClothOrder update(OrderStage orderStage, ClothOrder order, User user) {
        OrderStatus status = orderStatusService.create(orderStage, user);
        switch (orderStage) {
            case CANCELLED:
            case DECLINED_BY_CLIENT:
                order.setActive(false);
                break;
            case ASSIGNED_AND_WAITING_FOR_RETURN:
            default:
                order.setActive(true);
                break;
        }
        order.setOrderStatus(status);
        return order;
    }

    public List<ClothOrder> update(OrderStage orderStage, List<ClothOrder> clothOrders, User user) {
        Consumer<ClothOrder> changeStatus = order -> update(orderStage, order, user);
        clothOrders.stream().forEach(changeStatus);
        return clothOrders;
    }

    public List<ClothOrder> perform(ActionType actionType, List<ClothOrder> clothOrders, User user) {
        switch (actionType) {
            case ACCEPT:
                OrderStage accepted = PENDING_FOR_ASSIGNMENT;
                return update(accepted, clothOrders, user);
            case CANCEL:
                OrderStage cancelled = CANCELLED;
                return update(cancelled, clothOrders, user);
            default:
                return clothOrders;
        }
    }

    public ClothOrder cancel(ClothOrder order, User user) {
        return update(CANCELLED, order, user);
    }


    public ClothOrder update(ClothOrder order, ClothActualStatus clothStatus, User user) {
        if(order == null) {

        }
        OrderStatus orderStatus = null;
        switch (clothStatus) {
            case ORDERED:
            case ASSIGNED:
            case IN_PREPARATION:
            case RELEASED:
            case EXCHANGED:
            case ACCEPTED_AND_WITHDRAWN:
                break;
            case ACCEPTED_FOR_EXCHANGE:
                order = update(READY_FOR_REALIZATION, order, user);
                break;
        }
        return order;
    }
}
