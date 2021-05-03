package pl.bratosz.smartlockers.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Utils {

    public static <T> List<T> getDuplicates(Collection<T> collection) {
        return collection.stream().collect(Collectors.groupingBy(
                Function.identity()))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static <T> List<T> getOnlyUniqueValues(Collection<T> collection) {
        return collection.stream().collect(Collectors.groupingBy(
                Function.identity()))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() == 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static LocalDate convert(Date date)  {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
