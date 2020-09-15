package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderType {
    EXCHANGE_FOR_A_NEW_ONE("Wymiana na nowe"),
    CHANGE_SIZE("Zmiana rozmiaru"),
    CHANGE_ARTICLE("Zmiana artykułu"),
    NEW_ARTICLE("Nowy artykuł");

    private final String name;

    OrderType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

}