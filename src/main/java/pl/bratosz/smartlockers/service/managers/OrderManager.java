package pl.bratosz.smartlockers.service.managers;

import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothActualStatus;
import pl.bratosz.smartlockers.model.orders.*;
import pl.bratosz.smartlockers.model.orders.OrderStatus.OrderStage;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteOrderParameters;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteForExchangeAndRelease;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteForRelease;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.service.managers.creators.OrderCreator;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import static pl.bratosz.smartlockers.model.orders.OrderStatus.OrderStage.*;

public class OrderManager  {
    private User user;
    private Date date;

    public OrderManager(User user) {
        this.user = user;
        this.date = new Date();
    }

    public OrderManager(User user, Date date) {
        this.user = user;
        this.date = date;
    }

    public ClothOrder createOne(CompleteForRelease parameters) {
        return OrderCreator.create(parameters, user, date);
    }

    public ClothOrder createOne(CompleteForExchangeAndRelease parameters) {
        return OrderCreator.createWithExchange(parameters, user, date);
    }




    public ClothOrder createForExistingCloth(
            OrderType orderType,
            Cloth cloth
            ) {
        CompleteForRelease parameters = CompleteOrderParameters.createForExistingCloth(
                cloth,
                orderType,
                user);
        return create(parameters);
    }

    private ClothOrder create(
            CompleteForRelease parameters
    ) {
        ClothOrder order = new ClothOrder();
        order.setOrderType(parameters.getOrderType());
        order.setClothToRelease(parameters.getClothToRelease());

        OrderStatus status = new OrderStatus(parameters.getOrderStage(), user, date);
        order.setOrderStatus(status);

        return order;
    }


    public ClothOrder update(OrderStage orderStage, ClothOrder order) {
        OrderStatus status = new OrderStatus(orderStage, user, date);
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

    public List<ClothOrder> update(OrderStage orderStage, List<ClothOrder> clothOrders) {
        Consumer<ClothOrder> changeStatus = order -> update(orderStage, order);
        clothOrders.stream().forEach(changeStatus);
        return clothOrders;
    }

    public List<ClothOrder> perform(ActionType actionType, List<ClothOrder> clothOrders) {
        switch (actionType) {
            case ACCEPT:
                OrderStage accepted = PENDING_FOR_ASSIGNMENT;
                return update(accepted, clothOrders);
            case CANCEL:
                OrderStage cancelled = CANCELLED;
                return update(cancelled, clothOrders);
            default:
                return clothOrders;
        }
    }

    public ClothOrder cancel(ClothOrder order) {
        return update(CANCELLED, order);
    }


    public ClothOrder update(ClothOrder order, ClothActualStatus clothStatus) {
        OrderStatus orderStatus = null;
        switch (clothStatus) {
            case ORDERED:
                break;
            case ASSIGNED:
                break;
            case IN_PREPARATION:
                break;
            case RELEASED:
                break;
            case ACCEPTED_FOR_EXCHANGE:
                orderStatus = createActualStatus(READY_FOR_REALIZATION);
                break;
            case EXCHANGED:
                break;
            case WITHDRAWN:
                break;
        }
        order.setOrderStatus(orderStatus);
        return order;
    }

    private OrderStatus createActualStatus(OrderStage stage) {
        return new OrderStatus(stage, user, date);
    }


}
