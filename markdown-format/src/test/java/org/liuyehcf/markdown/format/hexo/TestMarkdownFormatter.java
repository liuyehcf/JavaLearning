package org.liuyehcf.markdown.format.hexo;

import org.junit.Test;

import java.lang.reflect.Method;

/**
 * Created by HCF on 2018/1/14.
 */
public class TestMarkdownFormatter {
    @Test
    public void testMain() throws Exception {
        Class clazz = MarkdownFormatter.class;
        Method mainMethod = clazz.getMethod("main", String[].class);
        String[] args = new String[]{
                "/Users/HCF/Desktop/source"
        };

        mainMethod.invoke(null, new Object[]{args});
    }
}
