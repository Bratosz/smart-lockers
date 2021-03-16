package pl.bratosz.smartlockers.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
}
