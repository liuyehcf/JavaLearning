package org.liuyehcf.markdown.format;

import org.junit.Test;

import java.lang.reflect.Method;

/**
 * Created by HCF on 2018/1/14.
 */
public class TestHexoFormatter {
    @Test
    public void testMain() throws Exception {
        Class clazz = HexoFormatter.class;
        Method mainMethod = clazz.getMethod("main", String[].class);
        String[] args = new String[]{
                "/Users/HCF/Desktop/source"
        };

        mainMethod.invoke(null, new Object[]{args});
    }
}
