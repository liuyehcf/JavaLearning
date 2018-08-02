package org.liuyehcf.flowalbe.config;

import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenlu
 * @date 2018/7/25
 */
@Configuration
public class BeanConfig {

    @Bean
    public ProcessEngineConfiguration processEngineConfiguration() {
        ProcessEngineConfiguration processEngineConfiguration = new StandaloneProcessEngineConfiguration();
        processEngineConfiguration.setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=1000");
        processEngineConfiguration.setJdbcDriver("org.h2.Driver");
        processEngineConfiguration.setJdbcUsername("sa");
        processEngineConfiguration.setJdbcPassword("");
        processEngineConfiguration.setDatabaseSchemaUpdate("true");
        processEngineConfiguration.setAsyncExecutorActivate(false);
        processEngineConfiguration.setMailServerHost("mail.my-crop.com");
        processEngineConfiguration.setMailServerPort(5025);
        return processEngineConfiguration;
    }
}
