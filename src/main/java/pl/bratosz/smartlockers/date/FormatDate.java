package pl.bratosz.smartlockers.date;

import pl.bratosz.smartlockers.exception.WrongDateFormatException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class FormatDate {
    String date;

    public static String getDate(Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(date);
    }

    public static Date getDate(String date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if(date.equals(null) || date.length() < 10) {
                return new Date(0);
            } else {
                return dateFormat.parse(date);
            }
        } catch (ParseException e) {
            e.getMessage();
            e.printStackTrace();
            throw new WrongDateFormatException("Proper date format is " +
                    "yyyy-MM-dd. Passed date was: " + date);
        }
    }
}
