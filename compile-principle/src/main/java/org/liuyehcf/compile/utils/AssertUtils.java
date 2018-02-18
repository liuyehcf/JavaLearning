package org.liuyehcf.compile.utils;

public class AssertUtils {
    public static void assertTrue(Boolean flag) {
        if (!flag) {
            throw new RuntimeException();
        }
    }

    public static void assertFalse(Boolean flag) {
        if (flag) {
            throw new RuntimeException();
        }
    }
}
