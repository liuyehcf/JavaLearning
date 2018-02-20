package org.liuyehcf.compile.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionUtils {
    public static <T> List<T> subListExceptFirstElement(List<T> list) {
        return list.subList(1, list.size());
    }

    public static <T> List<T> of(T... elements) {
        return new ArrayList<>((Arrays.asList(elements)));
    }

    public static <T> List<T> of(T element, List<T> list) {
        return of(list, element);
    }

    public static <T> List<T> of(List<T> list, T... elements) {
        List<T> newList = new ArrayList<>(list);
        newList.addAll(Arrays.asList(elements));
        return newList;
    }

    public static <T> List<T> of(List<T>... lists) {
        List<T> newList = new ArrayList<>();

        for (List<T> list : lists) {
            newList.addAll(list);
        }

        return newList;
    }
}
