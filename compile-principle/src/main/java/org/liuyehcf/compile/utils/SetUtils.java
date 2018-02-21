package org.liuyehcf.compile.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class SetUtils {
    public static <T> Set<T> of(T... elements) {
        return new HashSet<>((Arrays.asList(elements)));
    }

    public static <T> Set<T> extract(Set<T> set, T element) {
        Set<T> newSet = new HashSet<>(set);
        newSet.remove(element);
        return newSet;
    }

    public static <T> Set<T> of(Set<T>... sets) {
        Set<T> newSet = new HashSet<>();
        for (Set<T> set : sets) {
            newSet.addAll(set);
        }
        return newSet;
    }
}
