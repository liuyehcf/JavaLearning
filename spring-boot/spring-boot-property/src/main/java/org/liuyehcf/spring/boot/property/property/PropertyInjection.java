package org.liuyehcf.spring.boot.property.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author hechenfeng
 * @date 2019/2/27
 */
@Configuration
public class PropertyInjection {

    @Value("${normal.property1}")
    private String normalProperty1;

    @Value("${normal.property2}")
    private String normalProperty2;

    @Value("${extension.property1}")
    private String extensionProperty1;

    @Value("${extension.property2}")
    private String extensionProperty2;

    @Value("${runtime.property1}")
    private String runtimeProperty1;

    @Value("${runtime.property2}")
    private String runtimeProperty2;

    @PostConstruct
    public void printProperties() {
        System.out.println("normalProperty1 = " + normalProperty1);
        System.out.println("normalProperty2 = " + normalProperty2);
        System.out.println("extensionProperty1 = " + extensionProperty1);
        System.out.println("extensionProperty2 = " + extensionProperty2);
        System.out.println("runtimeProperty1 = " + runtimeProperty1);
        System.out.println("runtimeProperty2 = " + runtimeProperty2);
    }
}
