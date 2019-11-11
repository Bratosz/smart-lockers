package pl.bratosz.smartlockers.date;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CurrentDate {
    private String date;

    public CurrentDate() {
        date = dateTimeFormatter.format(now);
    }

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd");
    LocalDateTime now = LocalDateTime.now();

    public String getDate() {
        return date;
    }
}
