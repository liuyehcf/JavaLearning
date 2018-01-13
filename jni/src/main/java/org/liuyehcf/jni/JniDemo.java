package org.liuyehcf.jni;

/**
 * Created by HCF on 2018/1/13.
 */
public class JniDemo {
    public static void main(String[] args) {
        System.loadLibrary("Hello");
        sayHello();
    }

    private static native void sayHello();
}
