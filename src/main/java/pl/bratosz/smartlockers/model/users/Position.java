package pl.bratosz.smartlockers.model.users;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Position {
    WAREHOUSE_ASSISTANT("Magazynierka"),
    WAREHOUSE_LEADER("Kierowniczka magazynu"),
    SERVICE_ENGINEER("Serwisant"),
    SERVICE_LEADER("Kierownik serwisu"),
    SERVICE_DIRECTOR("Dyrektor serwisu");

    private final String name;

    Position(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
