package com.visualization.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.duckdb.DuckDBDriver;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @ConfigurationProperties(prefix = "spring.jpa")
    @Bean
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

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


    @Bean(value = "tableDatasource")
    public DataSource tableDatasource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:duckdb:");
        config.setDriverClassName(DuckDBDriver.class.getName());
        config.setUsername(null);
        config.setPassword(null);
        return new HikariDataSource(config);
    }


}


