package org.liuyehcf.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by HCF on 2018/1/19.
 */
public class LogbackDemo {
    private static final Logger CUSTOMIZE_NAME_LOGGER = LoggerFactory.getLogger("MyLogger");
    private static final Logger CLASS_LOGGER = LoggerFactory.getLogger(LogbackDemo.class);


    public static void main(String[] args) {
        CUSTOMIZE_NAME_LOGGER.error("CUSTOMIZE_NAME_LOGGER");

        CLASS_LOGGER.info("CLASS_LOGGER");
    }
}
