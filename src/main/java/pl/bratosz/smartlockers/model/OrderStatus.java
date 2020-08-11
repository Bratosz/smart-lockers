package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Entity;


public enum OrderStatus {
    DECLINED("odrzucone"),
    ACCEPTED("przyjęte"),
    INPROGRESS("w realizacji"),
    FINALIZED("zakończone");

    private final String name;

    OrderStatus(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
