package org.liuyehcf.classloader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by HCF on 2018/1/6.
 * 位于下层的ClassLoader
 */
public class UpLevelClassLoader extends AbstractClassLoader {


    public static void main(String[] args) throws Exception {
        ClassLoader classLoader = new UpLevelClassLoader();

        Class clazz = classLoader.loadClass("org.liuyehcf.datastructure.tree.bplustree.BPlusTree");

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
