package org.liuyehcf.compile.utils;

public abstract class AssertUtils {
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

    public static void assertNotNull(Object obj) {
        if (obj == null) {
            throw new AssertionError();
        }
    }

    public static void assertEquals(Object expect, Object target) {
        assertNotNull(expect);
        assertNotNull(target);
        if (!expect.equals(target)) {
            throw new AssertionError();
        }
    }
}
