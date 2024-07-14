package com.visualisation.handler;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public class JDBCHandler {

    private final DataSourceProperties dataSourceProperties;

    private DataSource dataSource;

    private NamedParameterJdbcTemplate jdbcTemplate;

    private String dialectId;

    public JDBCHandler(Map<String, String> properties) {
        String appDataSourceName = properties.get("appDataSourceName");
        this.dataSourceProperties = new DataSourceProperties();
        if (appDataSourceName != null) {
            DataSource ds = SpringApplicationHandler.getCtx().getBean(appDataSourceName, DataSource.class);
            this.dataSource = ds;
            try {
                Connection connection = ds.getConnection();
                DatabaseMetaData metaData = connection.getMetaData();
                dataSourceProperties.url = metaData.getURL();
                dataSourceProperties.username = metaData.getUserName();
                connection.close();
                jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            dataSourceProperties.url = properties.get("url");
            dataSourceProperties.username = properties.get("username");
            dataSourceProperties.password = properties.get("password");
        }
        DatabaseDriver databaseDriver = DatabaseDriver.fromJdbcUrl(dataSourceProperties.getUrl());
        this.dialectId = databaseDriver.getId();
    }

    public String getDialectId() {
        return dialectId;
    }

    public NamedParameterJdbcTemplate getJdbcTemplate() {
        if (Objects.isNull(dataSource)) {
            synchronized (this) {
                if (Objects.isNull(dataSource)) {
                    dataSource = DataSourceBuilder.create()
                            .url(dataSourceProperties.getUrl())
                            .username(dataSourceProperties.getUsername())
                            .password(dataSourceProperties.getPassword())
                            .driverClassName(DatabaseDriver.fromJdbcUrl(dataSourceProperties.getUrl()).getDriverClassName())
                            .build();
                    jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
                }
            }
        }
        return jdbcTemplate;
    }

    private static class DataSourceProperties {
        private String url;

        private String username;

        private String password;

        private String driverClassName;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }
    }
}
