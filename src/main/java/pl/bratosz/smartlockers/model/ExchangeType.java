package pl.bratosz.smartlockers.model;

public enum ExchangeType {
    CHANGE_SIZE("Zmiana rozmiaru"),
    FOR_A_NEW_ONE("Wymiana na nowe"),
    CHANGE_ARTICLE("Zmiana artykułu");

    private final String name;

    ExchangeType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
