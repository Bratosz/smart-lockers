package pl.bratosz.smartlockers.model.orders;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderType {
    AUTO_EXCHANGE("Wymiana automatyczna"),
    EXCHANGE_FOR_A_NEW_ONE("Wymiana na nowe"),
    CHANGE_SIZE("Zmiana rozmiaru"),
    CHANGE_ARTICLE("Zmiana artykułu"),
    NEW_ARTICLE("Nowy artykuł"),
    RELEASE("Wydanie"),
    EMPTY("Brak aktywnego zamówienia");

    private final String name;

    OrderType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

}