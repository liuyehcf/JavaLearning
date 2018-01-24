package org.liuyehcf.error;

public class NoClassDefFoundErrorDemo {
    public static void main(String[] args) {
        try {
            Class.forName("org.liuyehcf.error.InitializeThrowError");
        } catch (Throwable e) {
            // 这里捕获到的是java.lang.ExceptionInInitializerError
            e.printStackTrace();
        }

        System.out.println(InitializeThrowError.i);

    }
}

class InitializeThrowError {
    public static final int i = initError();

    private static int initError() {
        throw new RuntimeException("Initialize Error");
    }

    public void sayHello() {
        System.out.println("hello, world!");
    }
}
