package pl.bratosz.smartlockers.service.managers.creators;

import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.clothes.Cloth;
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
            CompleteForRelease params, User user) {
        return builder(user, new Date())
                .orderStatus(params.getOrderStatus())
                .orderType(params.getOrderType())
                .aNewCloth(params.getClothToRelease())
                .employee(params.getEmployee())
                .build();
    }

    public static ClothOrder createWithExchange(
            CompleteForExchangeAndRelease params, User user) {
        return builder(user, new Date())
                .orderStatus(params.getOrderStatus())
                .orderType(params.getOrderType())
                .replacingCloth(params.getClothToRelease())
                .clothToReplace(params.getClothToExchange())
                .employee(params.getEmployee())
                .build();

    }

    private static class Builder
            implements NeedOrderStatus,
            NeedOrderType,
            NeedClothToExchange,
            NeedClothToRelease,
            NeedEmployee,
            CanBeBuild {
        private User user;
        private Date date;
        private Cloth clothToExchange;
        private Cloth clothToRelease;
        private Employee employee;
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
            order.setEmployee(clothToRelease.getEmployee());
            order.setOrderType(orderType);
            order.setNote(note);
            order.setActive(true);
            return order;
        }


        @Override
        public Builder orderStatus(OrderStatus orderStatus) {
            this.orderStatus = orderStatus;
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

        @Override
        public Builder employee(Employee employee) {
            this.employee = employee;
            return this;
        }
    }

    public interface NeedOrderStatus {
        NeedOrderType orderStatus(OrderStatus orderStatus);
    }

    public interface NeedOrderType {
        NeedClothToRelease orderType(OrderType orderType);
    }


    public interface NeedClothToRelease {
        NeedEmployee aNewCloth(Cloth clothToRelease);

        NeedClothToExchange replacingCloth(Cloth clothToRelease);
    }

    public interface NeedClothToExchange {
        NeedEmployee clothToReplace(Cloth clothToExchange);
    }

    public interface NeedEmployee {
        CanBeBuild employee(Employee employee);
    }

    public interface CanBeBuild {
        CanBeBuild note(String note);

        ClothOrder build();
    }

}
