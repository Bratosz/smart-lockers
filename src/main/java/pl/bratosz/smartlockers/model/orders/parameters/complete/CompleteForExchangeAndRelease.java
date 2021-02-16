package pl.bratosz.smartlockers.model.orders.parameters.complete;

import pl.bratosz.smartlockers.model.orders.OrderStatus;
import pl.bratosz.smartlockers.model.orders.parameters.basic.ParametersForExchangeAndRelease;

public interface CompleteForExchangeAndRelease extends ParametersForExchangeAndRelease {
    boolean isOrderActive();
    OrderStatus.OrderStage getOrderStage();
}
