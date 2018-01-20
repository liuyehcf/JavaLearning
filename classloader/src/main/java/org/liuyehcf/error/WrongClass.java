package org.liuyehcf.error;

/**
 * 该类无法正常初始化
 */
public class WrongClass {
    public static final WrongClass INSTANCE = new WrongClass();

    public WrongClass() {
        throw new RuntimeException();
    }

    public void sayHello() {
        System.out.println("hello, world!");
    }
}
