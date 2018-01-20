package org.liuyehcf.error;

public class NoClassDefFoundErrorDemo {
    public static void main(String[] args) {
        System.out.println("加载类");
        try {
            Class.forName("org.liuyehcf.error.WrongClass");
        } catch (Throwable e) {
            // 这里必须用Throwable吃掉
            e.printStackTrace();
        }

        System.out.println("使用");
        try {
            WrongClass.INSTANCE.sayHello();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
