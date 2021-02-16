package pl.bratosz.smartlockers.model.users.roles;

import pl.bratosz.smartlockers.model.orders.ClothOrder;
import pl.bratosz.smartlockers.model.orders.parameters.basic.ParametersForExchangeAndRelease;
import pl.bratosz.smartlockers.model.orders.parameters.basic.ParametersForRelease;

public interface UserRole {
    ClothOrder createOrder(ParametersForExchangeAndRelease basicOrderParameters);
    ClothOrder createOrder(ParametersForRelease basicOrderParameters);
}
