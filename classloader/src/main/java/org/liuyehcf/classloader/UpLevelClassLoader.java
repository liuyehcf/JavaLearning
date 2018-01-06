package org.liuyehcf.classloader;

/**
 * Created by HCF on 2018/1/6.
 * 位于下层的ClassLoader
 */
public class UpLevelClassLoader extends AbstractClassLoader {


    public static void main(String[] args) throws Exception {
        ClassLoader classLoader = new UpLevelClassLoader();

        Class clazz = classLoader.loadClass(TEST_CLASS);

        AbstractClassLoader.testTemplate(clazz);
    }
}
