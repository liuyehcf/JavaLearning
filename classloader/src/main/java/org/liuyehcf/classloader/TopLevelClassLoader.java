package org.liuyehcf.classloader;

import java.lang.reflect.Field;

/**
 * Created by HCF on 2018/1/6.
 * 位于上层的ClassLoader，破坏了原有的双亲委派结构，插入到Ext与Bootstrap类加载器之间
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
        Class clazz = Class.forName(TEST_CLASS);

        AbstractClassLoader.testTemplate(clazz);
    }


}
