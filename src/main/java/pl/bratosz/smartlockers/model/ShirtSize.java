package pl.bratosz.smartlockers.model;

public enum ShirtSize {
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

    ShirtSize(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
