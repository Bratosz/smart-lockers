package pl.bratosz.smartlockers.model.users.roles;

import static pl.bratosz.smartlockers.model.orders.OrderStatus.*;



public class RoleClientEmployee extends PlainUserRole implements UserRole {
    private final OrderStage initialStage =
            OrderStage.REQUESTED_AND_PENDING_FOR_CONFIRMATION_BY_SUPERVISOR;
}
