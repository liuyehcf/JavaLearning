package org.liuyehcf.akka.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author hechenfeng
 * @date 2019/1/25
 */
public abstract class AkkaConfigUtils {
    public static String loadConfig(String classpath) {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        URL resource = systemClassLoader.getResource(classpath);

        if (resource == null) {
            throw new NullPointerException();
        }

        File file = new File(resource.getFile());
        StringBuilder sb = new StringBuilder();
        try (InputStream inputStream = new FileInputStream(file)) {
            int c;
            while ((c = inputStream.read()) != -1) {
                sb.append((char) c);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sb.toString();
    }
}
