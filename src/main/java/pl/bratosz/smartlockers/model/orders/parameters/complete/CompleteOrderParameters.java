package pl.bratosz.smartlockers.model.orders.parameters.complete;

import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.orders.OrderType;
import pl.bratosz.smartlockers.model.orders.parameters.basic.BasicOrderParameters;
import pl.bratosz.smartlockers.model.orders.parameters.basic.ParametersForExchangeAndRelease;
import pl.bratosz.smartlockers.model.orders.parameters.basic.ParametersForRelease;
import pl.bratosz.smartlockers.model.users.User;

import static pl.bratosz.smartlockers.model.orders.OrderStatus.*;

public class CompleteOrderParameters extends BasicOrderParameters
        implements
        CompleteForExchangeAndRelease, CompleteForRelease {

    private OrderStage orderStage;
    private boolean orderActive;


    public static CompleteForExchangeAndRelease createForClothExchangeAndRelease(
            Cloth clothForExchange,
            Cloth clothForRelease,
            Employee employee,
            OrderType orderType,
            User user
    ) {
        return new CompleteOrderParameters(
                clothForExchange,
                clothForRelease,
                employee,
                orderType,
                user.getInitialStageForOrders(),
                user
        );
    }


    public static CompleteForRelease createForNewArticle(
            Cloth clothForRelease,
            Employee employee,
            OrderStage orderStage,
            User user
    ) {
        OrderType orderType = OrderType.NEW_ARTICLE;
        return new CompleteOrderParameters(
                clothForRelease,
                employee,
                orderType,
                orderStage,
                user
        );
    }


    public static CompleteForRelease createForNewArticle(
            ParametersForRelease parameters,
            OrderStage orderStage
    ) {
        return createForNewArticle(
                parameters.getClothToRelease(),
                parameters.getEmployee(),
                orderStage,
                parameters.getUser()
        );
    }


    public static CompleteForRelease createOrderForExistingCloth(
            Cloth cloth,
            Employee employee,
            OrderType orderType,
            User user
    ) {
        OrderStage stage = resolveOrderStage(orderType);
        return new CompleteOrderParameters(
                cloth,
                employee,
                orderType,
                stage,
                user
        );
    }


    private CompleteOrderParameters(Cloth clothForRelease,
                                    Employee employee,
                                    OrderType orderType,
                                    OrderStage orderStage,
                                    User user
    ) {
        super(clothForRelease, employee, orderType, user);
        this.orderStage = orderStage;
    }


    private CompleteOrderParameters(Cloth clothForExchange,
                                    Cloth clothForRelease,
                                    Employee employee,
                                    OrderType orderType,
                                    OrderStage orderStage,
                                    User user
    ) {
        super(clothForExchange, clothForRelease, employee, orderType, user);
        this.orderStage = orderStage;
    }


    private static OrderStage resolveOrderStage(OrderType orderType) {
        if (orderType.equals(OrderType.EMPTY)) {
            return OrderStage.EMPTY;
        } else {
            return OrderStage.READY_FOR_REALIZATION;
        }
    }


    public OrderStage getOrderStage() {
        return orderStage;
    }


    public boolean isOrderActive() {
        return orderActive;
    }
}
