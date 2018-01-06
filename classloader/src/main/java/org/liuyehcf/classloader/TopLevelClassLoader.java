package org.liuyehcf.classloader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by HCF on 2018/1/6.
 */
public class TopLevelClassLoader extends AbstractClassLoader {
    private static TopLevelClassLoader instance = new TopLevelClassLoader();

    static {
        breakParentAgent();
    }

    public static TopLevelClassLoader getInstance() {
        return instance;
    }

    private static void breakParentAgent() {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        ClassLoader extClassLoader = systemClassLoader.getParent();
        ClassLoader bootstrapClassLoader = extClassLoader.getParent();

        try {
            Field parentField = ClassLoader.class.getDeclaredField("parent");

            for (Field field : systemClassLoader.getClass().getFields()) {
                System.out.println(field);
            }

            parentField.setAccessible(true);

            // 更改双亲委派，将我自己的类加载器插入到ext与boot之间
            parentField.set(extClassLoader, instance);
            parentField.set(instance, bootstrapClassLoader);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void main(String[] args) throws Exception {

        Class clazz = Class.forName("org.liuyehcf.datastructure.tree.bplustree.BPlusTree");

        Constructor constructor = clazz.getConstructor(int.class);

        Object obj = constructor.newInstance(5);

        System.err.println("ClassLoader: " + obj.getClass().getClassLoader());

        Method methodInsert = clazz.getMethod("insert", int.class);
        Method methodPrint = clazz.getMethod("levelOrderTraverse");

        methodInsert.invoke(obj, 3);
        methodInsert.invoke(obj, 4);
        methodInsert.invoke(obj, 5);
        methodPrint.invoke(obj);
    }
}
