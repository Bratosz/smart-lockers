package pl.bratosz.smartlockers.formaters;

public class StringFormater {

    public String capitalizeFirstLetter(String s) {
        if (s.equals(null) || s.length() == 1) return "null";
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
