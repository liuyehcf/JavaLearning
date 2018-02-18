package org.liuyehcf.compile.utils;

public class AssertUtils {
    public static void assertTrue(Boolean flag) {
        if (!flag) {
            throw new AssertionError();
        }
    }

    public static void assertFalse(Boolean flag) {
        if (flag) {
            throw new AssertionError();
        }
    }
}
