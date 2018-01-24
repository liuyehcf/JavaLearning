package org.liuyehcf.concurrent.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by liuye on 2017/4/8 0008.
 */
public class GetUnsafe {
    public static void main(String[] args) {
        Unsafe unsafe = getUnsafe();
    }

    public static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            return null;
        }
    }
}
