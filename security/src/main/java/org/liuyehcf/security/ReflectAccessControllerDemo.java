package org.liuyehcf.security;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by HCF on 2018/1/10.
 */
public class ReflectAccessControllerDemo {

    private static final String TARGET_DIR = "/Users/HCF/Workspaces/IdeaWorkspace/JavaLearning/security/src/main/resources/targetDir";
    private static final String JAR = "/Users/HCF/Workspaces/IdeaWorkspace/JavaLearning/security/src/main/resources/security-1.0-SNAPSHOT.jar";
    private static final String TEST_CLASS = "org.liuyehcf.security.FileUtils";

    public static void main(String[] args) {
        Class clazz;
        try {
            clazz = new URLClassLoader(
                    new URL[]{
                            new URL("file:" + JAR)
                    }).loadClass(TEST_CLASS);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        Method createFileMethod;
        Method createFilePrivilegeMethod;
        try {
            createFileMethod = clazz.getMethod("createFile", String.class, String.class);
            createFilePrivilegeMethod = clazz.getMethod("createFilePrivilege", String.class, String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }

        System.setSecurityManager(new SecurityManager());

        try {
            createFileMethod.invoke(null, TARGET_DIR, "file1.md");
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            createFilePrivilegeMethod.invoke(null, TARGET_DIR, "file2.md");
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
