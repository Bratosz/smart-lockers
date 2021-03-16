package pl.bratosz.smartlockers.service.managers.creators;

import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothSize;
import pl.bratosz.smartlockers.model.orders.*;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteForRelease;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteForExchangeAndRelease;
import pl.bratosz.smartlockers.model.users.User;

import java.util.Date;
import java.util.LinkedList;

public class OrderCreator {
    public static NeedOrderStatus builder(User user, Date date) {
        return new Builder(user, date);
    }

    public static ClothOrder create(
            CompleteForRelease params, User user, Date date) {
        return builder(user, date)
                .orderStatus(params.getOrderStage())
                .orderType(params.getOrderType())
                .aNewCloth(params.getClothToRelease())
                .build();
    }

    public static ClothOrder createWithExchange(
            CompleteForExchangeAndRelease params, User user, Date date) {
        return builder(user, date)
                .orderStatus(params.getOrderStage())
                .orderType(params.getOrderType())
                .replacingCloth(params.getClothToRelease())
                .clothToReplace(params.getClothToExchange())
                .note("")
                .build();

    }

    private static class Builder
            implements NeedOrderStatus, NeedOrderType, NeedClothToExchange, NeedClothToRelease, CanBeBuild {
        private User user;
        private Date date;
        private Cloth clothToExchange;
        private Cloth clothToRelease;
        private OrderStatus orderStatus;
        private OrderType orderType;
        private String note;

        public Builder(User user, Date date) {
            this.user = user;
            this.date = date;
        }

        @Override
        public CanBeBuild note(String note) {
            this.note = note;
            return this;
        }

        @Override
        public ClothOrder build() {
            ClothOrder order = new ClothOrder();
            order.setOrderStatusHistory(new LinkedList<>());
            order.setOrderStatus(orderStatus);
            order.setClothToExchange(clothToExchange);
            order.setClothToRelease(clothToRelease);
            order.setOrderType(orderType);
            order.setNote(note);
            order.setActive(true);
            return order;
        }


        @Override
        public Builder orderStatus(OrderStatus.OrderStage orderStage) {
            this.orderStatus = new OrderStatus(orderStage, user, date);
            return this;
        }

        @Override
        public Builder orderType(OrderType orderType) {
            this.orderType = orderType;
            return this;
        }

        @Override
        public Builder aNewCloth(Cloth clothToRelease) {
            this.clothToRelease = clothToRelease;
            this.clothToExchange = clothToRelease;
            return this;
        }

        @Override
        public Builder replacingCloth(Cloth clothToRelease) {
            this.clothToRelease = clothToRelease;
            return this;
        }

        @Override
        public Builder clothToReplace(Cloth clothToExchange) {
            this.clothToExchange = clothToExchange;
            return this;
        }
    }

    public interface NeedOrderStatus {
        NeedOrderType orderStatus(OrderStatus.OrderStage orderStage);
    }

    public interface NeedOrderType {
        NeedClothToRelease orderType(OrderType orderType);
    }


    public interface NeedClothToRelease {
        CanBeBuild aNewCloth(Cloth clothToRelease);

        NeedClothToExchange replacingCloth(Cloth clothToRelease);
    }

    public interface NeedClothToExchange {
        CanBeBuild clothToReplace(Cloth clothToExchange);
    }

    public interface CanBeBuild {
        CanBeBuild note(String note);

        ClothOrder build();
    }

}
