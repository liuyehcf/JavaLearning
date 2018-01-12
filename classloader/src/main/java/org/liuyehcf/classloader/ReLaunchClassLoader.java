package org.liuyehcf.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by HCF on 2018/1/12.
 */
public class ReLaunchClassLoader extends URLClassLoader {
    private static final ClassLoader extClassLoader = ClassLoader.getSystemClassLoader().getParent();

    public ReLaunchClassLoader(URL[] urls) {
        super(urls, null);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            return extClassLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            // ignore
        }

        return findClass(name);
    }
}
