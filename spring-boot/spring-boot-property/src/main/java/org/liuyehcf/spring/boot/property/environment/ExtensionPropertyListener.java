package org.liuyehcf.spring.boot.property.environment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author chenlu
 * @date 2019/2/27
 */
public class ExtensionPropertyListener implements EnvironmentPostProcessor {

    private static final String PROPERTY_PATH = "classpath:extension.properties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            MutablePropertySources propertySources = environment.getPropertySources();

            InputStream inputStream = new FileInputStream(ResourceUtils.getURL(PROPERTY_PATH).getPath());

            Properties properties = new Properties();

            // load properties from extension file
            properties.load(inputStream);

            // add properties
            properties.setProperty("runtime.property1", "runtime-property-1");
            properties.setProperty("runtime.property2", "runtime-property-2");

            PropertiesPropertySource propertySource = new PropertiesPropertySource("demoPropertySource", properties);

            propertySources.addLast(propertySource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
