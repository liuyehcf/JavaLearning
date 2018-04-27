package org.liuyehcf.classloader.error;

public class NoClassDefFoundErrorDemo {
    public static void main(String[] args) {
        try {
            Class.forName("org.liuyehcf.classloader.error.InitializeThrowError");
        } catch (Throwable e) {
            // 这里捕获到的是java.lang.ExceptionInInitializerError
            e.printStackTrace();
        }

        new InitializeThrowError();
    }
}

class InitializeThrowError {

    static {
        error();
    }

    private static void error() {
        throw new RuntimeException("Initialize Error");
    }
}
