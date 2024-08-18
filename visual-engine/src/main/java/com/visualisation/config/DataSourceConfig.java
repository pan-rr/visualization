package com.visualisation.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @ConfigurationProperties(prefix = "visual.db")
    @Bean(value = "db")
    public DataSource db() {
        return new HikariDataSource();
    }


    @Primary
    @ConfigurationProperties(prefix = "visual.dag")
    @Bean(value = "dag")
    public DataSource dag() {
        return new HikariDataSource();
    }


}


