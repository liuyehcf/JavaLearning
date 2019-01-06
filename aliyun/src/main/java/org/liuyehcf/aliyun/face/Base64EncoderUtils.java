package org.liuyehcf.aliyun.face;

import sun.misc.BASE64Encoder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by HCF on 2017/12/16.
 */
public class Base64EncoderUtils {
    public static String encodeWithPath(String path) {
        File file = new File(path);

        BufferedInputStream inputStream;
        try {
            inputStream =
                    new BufferedInputStream(new FileInputStream(file));
        } catch (IOException e) {
            System.err.println("文件路径错误");
            throw new RuntimeException(e);
        }

        long fileLength = file.length();

        if (fileLength > Integer.MAX_VALUE) {
            throw new RuntimeException("文件长度超过Integer.MAX_VALUE");
        }

        byte[] bytes = new byte[(int) fileLength];
        try {
            inputStream.read(bytes);
        } catch (IOException e) {
            System.err.println("IO错误");
            throw new RuntimeException(e);
        }

        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(bytes);
    }
}
