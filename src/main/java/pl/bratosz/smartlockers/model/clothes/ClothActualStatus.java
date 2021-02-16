package pl.bratosz.smartlockers.model.clothes;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ClothActualStatus {
    ORDERED("Zamówione", false),
    ASSIGNED("Przypisane", false),
    PENDING_FOR_SUPPLY("Oczekuje na dostawę", false),
    IN_PREPARATION("W przygotowaniu", false),
    RELEASED("Wydane", true),
    ACCEPTED_FOR_EXCHANGE("Przyjęte do wymiany", true),
    EXCHANGED("Wymienione", false),
    WITHDRAWN("Wycofane", false);

    private String name;
    private boolean inRotation;

    ClothActualStatus(String name, boolean inRotation) {
        this.name = name;
        this.inRotation = inRotation;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonValue
    public boolean isInRotation() {
        return inRotation;
    }
}
