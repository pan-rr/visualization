package com.visualization.service;

import com.influxdb.client.InfluxDBClient;
import com.visualization.model.portal.metric.Metric;
import com.visualization.model.portal.metric.MetricCalculator;
import com.visualization.repository.dag.DAGTemplateRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Service
public class MetricService {

    @Resource(name = "visualInfluxDBClient")
    private InfluxDBClient influxDBClient;

    @Resource(name = "dag")
    private DataSource dataSource;

    @Resource
    private MinIOService minIOService;

    @Resource
    private DAGTemplateRepository dagTemplateRepository;


    public Metric getMetrics(Metric metric) {
        MetricCalculator calculator = MetricCalculator.builder()
                .influxDBClient(influxDBClient)
                .minIOService(minIOService)
                .jdbcTemplate(new NamedParameterJdbcTemplate(dataSource))
                .dagTemplateRepository(dagTemplateRepository)
                .res(metric)
                .build();
        return calculator.calculate();
    }



}
