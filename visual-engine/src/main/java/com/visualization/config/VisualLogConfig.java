package com.visualization.config;

import com.visualization.log.logger.VisualLogService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VisualLogConfig {

    @Value("${visual.log.loggerClass:com.visualisation.log.logger.NormalLogService}")
    private String loggerClassName ;

    @SneakyThrows
    @Bean
    VisualLogService visualLogger(){
        Class<?> loggerClass = getClass().getClassLoader().loadClass(loggerClassName);
        return (VisualLogService) loggerClass.newInstance();
    }
}
