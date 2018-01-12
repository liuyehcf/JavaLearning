package org.liuyehcf.classloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by HCF on 2018/1/12.
 */
public class TraceForName {

    private static final String INITIALIZE_CONTROL_KEY = "INITIALIZE_CONTROL_KEY";

    public static void main(String[] args) {
        System.err.println("ClassLoader: " + TraceForName.class.getClassLoader());
        if (System.getProperty(INITIALIZE_CONTROL_KEY) == null
                || System.getProperty(INITIALIZE_CONTROL_KEY).equals("false")) {
            reLaunch(args);
        }

        try {
//            TraceForName.class.getClassLoader().loadClass("org.liuyehcf.classloader.SubObject");
            Class.forName("org.liuyehcf.classloader.SubObject");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void reLaunch(String[] args) {
        System.setProperty(INITIALIZE_CONTROL_KEY, "true");

        ReLaunchClassLoader reLaunchClassLoader = new ReLaunchClassLoader(getUrls());

        Class clazz;
        try {
            clazz = reLaunchClassLoader.loadClass("org.liuyehcf.classloader.TraceForName");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Method method;
        try {
            method = clazz.getMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 重新调用main函数
                    method.invoke(null, new Object[]{args});
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {

        }

        System.exit(0);

    }

    private static URL[] getUrls() {
        URL[] urls = ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs();

        URL[] newUrls = new URL[urls.length + 1];

        System.arraycopy(urls, 0, newUrls, 0, urls.length);

        newUrls[urls.length] = AbstractClassLoader.JAR_URL;

        return newUrls;
    }
}
