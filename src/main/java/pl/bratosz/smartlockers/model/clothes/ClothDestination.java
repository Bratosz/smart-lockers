package pl.bratosz.smartlockers.model.clothes;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ClothDestination {
    FOR_ASSIGN("Do przypisania"),
    FOR_RELEASE("Do wydania"),
    FOR_WASH("Do prania"),
    FOR_WITHDRAW_AND_DELETE("Do wycofania i usunięcia"),
    FOR_WITHDRAW_AND_EXCHANGE("Do wycofania i wymiany"),
    FOR_DISPOSAL("Do utylizacji");

    private final String name;

    ClothDestination(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
