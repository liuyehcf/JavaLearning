package org.liuyehcf.classloader;

/**
 * Created by HCF on 2018/1/6.
 */
public class IsolatedClassLoader extends AbstractClassLoader {

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {

        try {
            ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
            return systemClassLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            //ignore
        }

        return findClass(name);
    }


    public static void main(String[] args) throws Exception {
        ClassLoader classLoader = new IsolatedClassLoader();

        Class clazz = classLoader.loadClass(TEST_CLASS);

        AbstractClassLoader.testTemplate(clazz);
    }
}
