package org.liuyehcf.error;

public class NoClassDefFoundErrorDemo {
    public static void main(String[] args) {
        try {
            Class.forName("org.liuyehcf.error.InitializeThrowError");
        } catch (Throwable e) {
            // 这里捕获到的是java.lang.ExceptionInInitializerError
            e.printStackTrace();
        }

        try {
            InitializeThrowError.INSTANCE.sayHello();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

class InitializeThrowError {
    public static final InitializeThrowError INSTANCE = new InitializeThrowError();

    public InitializeThrowError() {
        throw new RuntimeException();
    }

    public void sayHello() {
        System.out.println("hello, world!");
    }
}
