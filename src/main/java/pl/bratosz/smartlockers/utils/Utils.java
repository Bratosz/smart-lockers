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

    public static List<Integer> findMissingNumbersFromRange(
            List<Integer> sortedList,
            int firstNumber,
            int lastNumber) {
        sortedList.add(lastNumber +1);
        int[] register = new int[lastNumber + 2];
        for(int i : sortedList) {
            register[i] = 1;
        }
        List<Integer> missingNumbers = new LinkedList<>();
        for(int i = firstNumber; i < register.length; i++) {
            if(register[i] == 0) {
                missingNumbers.add(i);
            }
        }
        return missingNumbers;
    }

    public static LocalDate convert(Date date)  {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static String substringBetween(String s, String before, String behind) {
        int from =  s.indexOf(before) + before.length();
        int to = s.lastIndexOf(behind);
        return s.substring(from, to);
    }
}
