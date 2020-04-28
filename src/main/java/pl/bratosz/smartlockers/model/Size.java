package pl.bratosz.smartlockers.model;

public enum Size {
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
    SIZE_56("56"),
    SIZE_58("58"),
    SIZE_60("60"),
    SIZE_62("62"),
    SIZE_64("64"),
    SIZE_66("66"),
    SIZE_XS("XS"),
    SIZE_S("S"),
    SIZE_M("M"),
    SIZE_L("L"),
    SIZE_XL("XL"),
    SIZE_XXL("2XL"),
    SIZE_XXXL("3XL"),
    SIZE_XXXXL("4XL"),
    SIZE_XXXXXL("5XL"),
    SIZE_XXXXXXL("6XL");

    private final String name;

    Size(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
