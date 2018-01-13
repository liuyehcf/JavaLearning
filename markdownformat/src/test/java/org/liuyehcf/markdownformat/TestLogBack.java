package org.liuyehcf.markdownformat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by HCF on 2018/1/13.
 */
public class TestLogBack {
    private static Logger logger = LoggerFactory.getLogger(TestLogBack.class);

    @Test
    public void testLog() {

        for (int i = 0; i < 10; i++) {
            logger.error(i + "");
        }
    }
}
