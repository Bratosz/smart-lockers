package pl.bratosz.smartlockers.response;

import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothAcceptanceType;
import pl.bratosz.smartlockers.model.orders.ClothOrder;
import pl.bratosz.smartlockers.model.orders.OrderType;

public class ResponseClothAcceptance {
    private boolean isFound;
    private boolean isAccepted;
    private String message;

    private ResponseClothAcceptance(String message){
        this.message = message;
    }

    public static ResponseClothAcceptance createClothAcceptedResponse(ClothOrder clothOrder) {
        ResponseClothAcceptance response =  new ResponseClothAcceptance("Zaakceptowano ubranie.\n" +
                clothOrder.toString());
        response.setAcceptedStatus();
        return response;
    }

    public static ResponseClothAcceptance createClothNotFound(long id) {
        ResponseClothAcceptance response = new ResponseClothAcceptance("Nie znaleziono ubrania o id: " + id);
        response.setNotFoundStatus();
        return response;
    }

    public static ResponseClothAcceptance createClothBelongsToOtherClient(Cloth cloth) {
        int plantNumber = cloth.getEmployee().getDepartment().getMainPlantNumber();
        ResponseClothAcceptance response = new ResponseClothAcceptance("Ubranie należy do innego klienta. Jego nr zakładu to: " + plantNumber);
        response.setBelongToOtherClientStatus();
        return response;
    }

    public static ResponseClothAcceptance createNoActiveOrderPresentResponse(Cloth cloth) {

        ResponseClothAcceptance response = new ResponseClothAcceptance("Brak aktywnego zlecenia dla ubrania.\n" +
                "Ubranie: " + cloth.toString());
        response.setNotAcceptedStatus();
        return response;
    }

    public static ResponseClothAcceptance createNewOrderAddedAndClothAcceptedResponse(ClothOrder clothOrder) {
        ResponseClothAcceptance response = new ResponseClothAcceptance("Utworzono nowe zlecenie i zaakceptowano ubranie.\n" +
                clothOrder.toString());
        response.setAcceptedStatus();
        return response;
    }

    public static ResponseClothAcceptance createAnotherOrderIsActiveResponse(ClothOrder clothOrder) {
        ResponseClothAcceptance response = new ResponseClothAcceptance("Aktywne jest inne zlecenie. Aby je usunąć udaj" +
                " się do widoku pracownika.\n" +
                "Aktywne zamówienie:\n" + clothOrder.toString());
        return response;
    }

    public static ResponseClothAcceptance createWrongAcceptanceTypeResponse(ClothAcceptanceType acceptanceType) {
        ResponseClothAcceptance response = new ResponseClothAcceptance("Błędny typ akceptacji odzieży: "
                + acceptanceType.getName());
        return response;
    }

    public static ResponseClothAcceptance wrongOrderTypeResponse(OrderType orderType) {
        ResponseClothAcceptance response = new ResponseClothAcceptance("Niewłaściwy typ zamówienia \n"
        + orderType.getName());
        return response;
    }

    private void setNotAcceptedStatus() {
        isFound = true;
        isAccepted = false;
    }

    private void setBelongToOtherClientStatus() {
        isFound = true;
        isAccepted = false;
    }

    private void setNotFoundStatus() {
        isFound = false;
        isAccepted = false;
    }

    private void setAcceptedStatus() {
        isFound = true;
        isAccepted = true;
    }


}
