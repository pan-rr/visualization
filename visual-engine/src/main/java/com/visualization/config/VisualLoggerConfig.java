package com.visualization.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VisualLoggerConfig {

    @Bean(name = "visualInfluxDBClient")
    public InfluxDBClient visualInfluxDBClient(@Value(value = "${visual.log.influx.connectionString}") String connectionString) {
        InfluxDBClientOptions options = InfluxDBClientOptions.builder()
                .connectionString(connectionString)
                .build();
        return InfluxDBClientFactory.create(options);
    }

}
