package org.liuyehcf.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Vector;

/**
 * Created by HCF on 2018/1/6.
 */
public abstract class AbstractClassLoader extends ClassLoader {
    private final String BASE_DIR = "classloader/src/main/lib";

    final static String TEST_CLASS = "org.liuyehcf.datastructure.tree.bplustree.BPlusTree";

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        File file = getClassFile(name);

        if (file.length() > Integer.MAX_VALUE) {
            throw new ClassNotFoundException("class file too large");
        }

        byte[] bytes = new byte[(int) file.length()];

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytes);
        } catch (IOException e) {
            throw new ClassNotFoundException(e.getMessage());
        }

        return super.defineClass(name, bytes, 0, bytes.length);
    }

    private File getClassFile(String name) throws ClassNotFoundException {
        String[] pathFragments = name.split("\\.");

        if (pathFragments.length == 0) {
            throw new ClassNotFoundException("Wrong ClassName: " + name);
        }

        StringBuilder sb = new StringBuilder();

        sb.append(BASE_DIR).append("/");

        for (int i = 0; i < pathFragments.length - 1; i++) {
            String pathFragment = pathFragments[i];
            sb.append(pathFragment);
            if (isDirectory(sb.toString())) {
                sb.append("/");
            } else {
                sb.append(".");
            }
        }

        sb.append(pathFragments[pathFragments.length - 1]).append(".class");

        File file = new File(sb.toString());

        if (!file.exists() || !file.isFile()) {
            throw new ClassNotFoundException("Wrong ClassName: " + name);
        }

        return file;
    }

    private boolean isDirectory(String path) {
        File file = new File(path);
        return file.exists() && file.isDirectory();
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
