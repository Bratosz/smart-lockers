package pl.bratosz.smartlockers.model.orders.parameters.complete;

import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.orders.OrderStatus;
import pl.bratosz.smartlockers.model.orders.OrderType;
import pl.bratosz.smartlockers.model.orders.parameters.basic.BasicOrderParameters;
import pl.bratosz.smartlockers.model.orders.parameters.basic.ParametersForExchangeAndRelease;
import pl.bratosz.smartlockers.model.orders.parameters.basic.ParametersForRelease;
import pl.bratosz.smartlockers.model.users.User;

import static pl.bratosz.smartlockers.model.orders.OrderStatus.*;

public class CompleteOrderParameters extends BasicOrderParameters
        implements
        CompleteForExchangeAndRelease, CompleteForRelease {

    private OrderStatus orderStatus;
    private boolean orderActive;


    public static CompleteForExchangeAndRelease createForClothExchangeAndRelease(
            Cloth clothForExchange,
            Cloth clothForRelease,
            Employee employee,
            OrderType orderType,
            OrderStatus orderStatus,
            User user
    ) {
        return new CompleteOrderParameters(
                clothForExchange,
                clothForRelease,
                employee,
                orderType,
                orderStatus,
                user
        );
    }


    public static CompleteForRelease createForNewArticle(
            Cloth clothForRelease,
            Employee employee,
            OrderStatus orderStatus,
            User user
    ) {
        OrderType orderType = OrderType.NEW_ARTICLE;
        return new CompleteOrderParameters(
                clothForRelease,
                employee,
                orderType,
                orderStatus,
                user
        );
    }


    public static CompleteForRelease createForNewArticle(
            ParametersForRelease parameters,
            OrderStatus orderStatus
    ) {
        return createForNewArticle(
                parameters.getClothToRelease(),
                parameters.getEmployee(),
                orderStatus,
                parameters.getUser()
        );
    }


    public static CompleteForRelease createOrderForExistingCloth(
            Cloth cloth,
            Employee employee,
            OrderType orderType,
            OrderStatus orderStatus,
            User user
    ) {
        return new CompleteOrderParameters(
                cloth,
                employee,
                orderType,
                orderStatus,
                user
        );
    }


    private CompleteOrderParameters(Cloth clothForRelease,
                                    Employee employee,
                                    OrderType orderType,
                                    OrderStatus orderStatus,
                                    User user
    ) {
        super(clothForRelease, employee, orderType, user);
        this.orderStatus = orderStatus;
    }


    private CompleteOrderParameters(Cloth clothForExchange,
                                    Cloth clothForRelease,
                                    Employee employee,
                                    OrderType orderType,
                                    OrderStatus orderStatus,
                                    User user
    ) {
        super(clothForExchange, clothForRelease, employee, orderType, user);
        this.orderStatus = orderStatus;

    }


    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public boolean isOrderActive() {
        return orderActive;
    }

}
