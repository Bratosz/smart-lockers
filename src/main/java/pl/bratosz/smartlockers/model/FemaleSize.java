package pl.bratosz.smartlockers.model;

public enum FemaleSize {
    SIZE_34("34"),
    SIZE_36("36"),
    SIZE_38("38"),
    SIZE_40("40"),
    SIZE_42("42"),
    SIZE_44("44"),
    SIZE_46("46"),
    SIZE_48("48"),
    SIZE_50("50"),
    SIZE_52("52"),
    SIZE_54("54"),
    SIZE_56("56");

    private final String name;

    FemaleSize(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
