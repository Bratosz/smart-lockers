package pl.bratosz.smartlockers.model.users.roles;

import pl.bratosz.smartlockers.model.orders.ClothOrder;
import pl.bratosz.smartlockers.model.orders.parameters.basic.ParametersForExchangeAndRelease;
import pl.bratosz.smartlockers.model.orders.parameters.basic.ParametersForRelease;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteOrderParameters;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteForExchangeAndRelease;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteForRelease;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.service.managers.OrderManager;


import javax.persistence.*;
import java.util.Date;

import static pl.bratosz.smartlockers.model.orders.OrderStatus.*;
//MAKE PLAIN USER ROLE BASIC ROLE, AND SET IT AS AN EMBEDDED CLASS
@Embeddable
public class PlainUserRole implements UserRole {

    private final OrderStage initialStage =
            OrderStage.REQUESTED_AND_PENDING_FOR_CONFIRMATION_BY_SUPERVISOR;

    @Override
    public ClothOrder createOrder(ParametersForRelease basicParameters) {
        User user = basicParameters.getUser();
        OrderManager orderManager = new OrderManager(user, new Date());
            CompleteForRelease newArticleParameters = CompleteOrderParameters
                    .createForNewArticle(basicParameters, initialStage);
            return orderManager.createOne(newArticleParameters);
    }

    @Override
    public ClothOrder createOrder(ParametersForExchangeAndRelease basicOrderParameters) {
        User user = basicOrderParameters.getUser();
        OrderManager orderManager = new OrderManager(user, new Date());
        CompleteForExchangeAndRelease completeParameters = CompleteOrderParameters
                .createForClothExchange(basicOrderParameters, initialStage);
        return orderManager.createOne(completeParameters);
    }
}
