package pl.bratosz.smartlockers.strings;

public class MyString {
    private String str;
    private MyString(String str) {
        this.str = normalize(str);
    }

    public static MyString create(String str){
        return new MyString(str);
    }

    private String normalize(String s) {
        return s.trim().toUpperCase();
    }

    public String capitalizeFirstLetter() {
        if (str.length() < 2) {
            return str.toUpperCase();
        } else {
            return str.substring(0, 1).toUpperCase()
                    + str.substring(1).toLowerCase();
        }
    }

    public String get() {
        return str;
    }
}
