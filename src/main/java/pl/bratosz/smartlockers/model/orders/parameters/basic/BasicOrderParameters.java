package pl.bratosz.smartlockers.model.orders.parameters.basic;

import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.orders.OrderType;
import pl.bratosz.smartlockers.model.users.User;

import java.util.Date;

public class BasicOrderParameters implements ParametersForExchangeAndRelease {
    protected Cloth clothForExchange;
    protected Cloth clothForRelease;
    protected OrderType orderType;
    protected User user;
    protected Date date;

    public static ParametersForExchangeAndRelease createForClothExchange(
            Cloth clothForExchange,
            Cloth clothForRelease,
            OrderType orderType,
            User user) {
        return new BasicOrderParameters(
                clothForExchange, clothForRelease, orderType, user);
    }

    public static ParametersForRelease createForNewArticle(
            Cloth clothForRelease,
            User user) {
        OrderType type = OrderType.NEW_ARTICLE;
        return new BasicOrderParameters(
                clothForRelease, type, user);
    }

    protected BasicOrderParameters(Cloth clothForExchange,
                                   Cloth clothForRelease,
                                   OrderType orderType,
                                   User user) {
        this.clothForExchange = clothForExchange;
        this.clothForRelease = clothForRelease;
        this.orderType = orderType;
        this.user = user;
        this.date = new Date();
    }

    protected BasicOrderParameters(Cloth clothForRelease,
                                OrderType orderType,
                                User user) {
        this.clothForRelease = clothForRelease;
        this.orderType = orderType;
        this.user = user;
        this.date = new Date();
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public User getUser() {
        return user;
    }

    public Date getDate() {
        return date;
    }

    public Cloth getClothToExchange() {
        return clothForExchange;
    }

    public Cloth getClothToRelease() {
        return clothForRelease;
    }
}
