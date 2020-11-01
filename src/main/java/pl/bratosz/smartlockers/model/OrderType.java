package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderType {
    AUTO_EXCHANGE("wymiana automatyczna"),
    EXCHANGE_FOR_A_NEW_ONE("wymiana na nowe"),
    CHANGE_SIZE("zmiana rozmiaru"),
    CHANGE_ARTICLE("zmiana artykułu"),
    NEW_ARTICLE("nowy artykuł"),
    NO_ACTIVE_ORDER("brak aktywnego zamówienia");

    private final String name;

    OrderType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

}