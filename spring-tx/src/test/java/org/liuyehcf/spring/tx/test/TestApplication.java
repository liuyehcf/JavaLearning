package org.liuyehcf.spring.tx.test;

import org.liuyehcf.spring.tx.Application;
import org.liuyehcf.spring.tx.DalConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * @author hechenfeng
 * @date 2018/8/26
 */
@SpringBootApplication(scanBasePackages = "org.liuyehcf.spring.tx")
@ComponentScan(basePackages = "org.liuyehcf.spring.tx",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {Application.class, DalConfig.class})
)
public class TestApplication {
}
