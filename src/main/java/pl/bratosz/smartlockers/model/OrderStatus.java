package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonValue;


public enum OrderStatus {
    REQUESTED_AND_PENDING_FOR_CONFIRMATION("oczekuje na potwierdzenie", 0),
    CONFIRMED_AND_PENDING_FOR_ACCEPTANCE("oczekuje na akceptację", 1),
    DECLINED_BY_CLIENT("odrzucone przez kilenta",1),
    RESTORED_BY_CLIENT("przywrócone przez klienta", 1),
    ACCEPTED_AND_PENDING_FOR_REALIZATION("oczekuje na wykonanie",2),
    DECLINED("odrzucone", 2),
    RESTORED("przywrócone", 2),
    DEFFERED("odroczone", 2),
    IN_REALIZATION("w realizacji",3),
    PREPARED("przygotowane", 4),
    PACKED("spakowane", 5),
    DELIVERED("dostarczone",6),
    FINALIZED("sfinalizowane", 7),
    CANCELLED("usunięte", -1);

    private final String name;
    private final int ordinalNumber;

    OrderStatus(String name, int ordinalNumber) {
        this.name = name;
        this.ordinalNumber = ordinalNumber;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    public int getOrdinalNumber() {return ordinalNumber;}
}
