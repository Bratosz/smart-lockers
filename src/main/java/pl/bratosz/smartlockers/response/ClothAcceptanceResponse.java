package pl.bratosz.smartlockers.response;

import pl.bratosz.smartlockers.model.ClothOrder;
import pl.bratosz.smartlockers.model.Employee;

public class ClothAcceptanceResponse {
    private boolean isAccepted;
    private ClothOrder clothOrder;
    private String message;

    public ClothAcceptanceResponse(ClothOrder clothOrder) {
        if(clothOrder.equals(null)){
            isAccepted = false;
            message = setMessage();
        } else {
            isAccepted = true;
            this.clothOrder = clothOrder;
            message = setMessage();
        }

    }

    private String setMessage() {
        if(isAccepted) {
            String message;
            Employee emp = clothOrder.getEmployee();

            message = "Przyjęto odzież. " +
                    emp.getbo
        }
    }
}
