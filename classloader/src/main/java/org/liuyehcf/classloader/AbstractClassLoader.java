package org.liuyehcf.classloader;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;

/**
 * Created by HCF on 2018/1/6.
 */
public abstract class AbstractClassLoader extends URLClassLoader {
    private static final String JAR_DIR = "file:./classloader/src/main/resources/algorithm-1.0-SNAPSHOT.jar";

    private static final URL JAR_URL;

    static {
        try {
            JAR_URL = new URL(JAR_DIR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    final static String TEST_CLASS = "org.liuyehcf.datastructure.tree.bplustree.BPlusTree";

    public AbstractClassLoader() {
        super(new URL[]{JAR_URL});
    }

    public static void testTemplate(Class clazz) throws Exception {
        Constructor constructor = clazz.getConstructor(int.class);

        Object obj = constructor.newInstance(5);

        System.err.println("ClassLoader: " + obj.getClass().getClassLoader());
        printAllLoadedClasses(obj.getClass().getClassLoader());

        Method methodInsert = clazz.getMethod("insert", int.class);
        Method methodPrint = clazz.getMethod("levelOrderTraverse");

        methodInsert.invoke(obj, 3);
        methodInsert.invoke(obj, 4);
        methodInsert.invoke(obj, 5);
        methodPrint.invoke(obj);
    }

    private static void printAllLoadedClasses(ClassLoader classLoader) throws Exception {
        Field field = ClassLoader.class.getDeclaredField("classes");
        field.setAccessible(true);

        Vector<Class<?>> classes = (Vector<Class<?>>) field.get(classLoader);

        for (Class clazz : classes) {
            System.err.println("loaded class: " + clazz);
        }
    }
}
