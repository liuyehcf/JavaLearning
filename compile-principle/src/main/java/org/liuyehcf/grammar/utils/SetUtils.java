package org.liuyehcf.grammar.utils;

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

    public static <T> Set<T> of(Set<T> set1, Set<T> set2) {
        Set<T> newSet = new HashSet<>(set1);

        newSet.addAll(set2);

        return newSet;
    }
}
