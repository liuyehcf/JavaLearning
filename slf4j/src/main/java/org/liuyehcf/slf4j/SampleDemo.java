package org.liuyehcf.slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by HCF on 2017/12/12.
 */
public class SampleDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleDemo.class);

    public static void main(String[] args) {
        LOGGER.debug("Description: {}", "Log Successfully!");
    }
}
