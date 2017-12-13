package org.liuyehcf.format.markdown;

import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by HCF on 2017/12/3.
 */
public class TestEngine {
    @Test
    public void testMain() {


        File baseDir = new File("./target/test-classes");
        System.out.println(baseDir.getAbsolutePath());
        String param = "false";

        if (!baseDir.isDirectory())
            throw new RuntimeException("Please type correct base path");

        if (!param.equalsIgnoreCase("true") && !param.equalsIgnoreCase("false")) {
            throw new RuntimeException("Please type correct param");
        }

        File[] files = baseDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".md");
            }
        });

        FormatEngine engine = new FormatEngine(param.equalsIgnoreCase("true"), files);

        for (File file : files) {
            engine.format(file);
        }

        System.out.println("finished");
    }

}
