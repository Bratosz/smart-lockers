package pl.bratosz.smartlockers.model.orders.parameters.complete;

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


    public static CompleteForExchangeAndRelease createForClothExchange(
            Cloth clothForExchange,
            Cloth clothForRelease,
            OrderType orderType,
            OrderStage orderStage,
            User user
    ) {
        return new CompleteOrderParameters(
                clothForExchange,
                clothForRelease,
                orderType,
                orderStage,
                user
        );
    }


    public static CompleteForExchangeAndRelease createForClothExchange(
            ParametersForExchangeAndRelease basicParameters, OrderStage orderStage) {
        return createForClothExchange(
                basicParameters.getClothToExchange(),
                basicParameters.getClothToRelease(),
                basicParameters.getOrderType(),
                orderStage,
                basicParameters.getUser()
        );
    }


    public static CompleteForRelease createForNewArticle(
            Cloth clothForRelease,
            OrderStage orderStage,
            User user
    ) {
        OrderType orderType = OrderType.NEW_ARTICLE;
        return new CompleteOrderParameters(
                clothForRelease,
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
                orderStage,
                parameters.getUser()
        );
    }


    public static CompleteForRelease createForExistingCloth(
            Cloth cloth,
            OrderType orderType,
            User user
    ) {
        OrderStage stage = resolveOrderStage(orderType);
        return new CompleteOrderParameters(
                cloth,
                orderType,
                stage,
                user
        );
    }


    private CompleteOrderParameters(Cloth clothForRelease,
                                    OrderType orderType,
                                    OrderStage orderStage,
                                    User user
    ) {
        super(clothForRelease, orderType, user);
        this.orderStage = orderStage;
    }


    private CompleteOrderParameters(Cloth clothForExchange,
                                    Cloth clothForRelease,
                                    OrderType orderType,
                                    OrderStage orderStage,
                                    User user
    ) {
        super(clothForExchange, clothForRelease, orderType, user);
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
